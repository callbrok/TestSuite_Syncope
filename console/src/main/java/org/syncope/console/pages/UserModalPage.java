/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.syncope.console.pages;

import org.syncope.console.pages.panels.AttributesPanel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authorization.strategies.role.metadata.MetaDataRoleAuthorizationStrategy;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainerWithAssociatedMarkup;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.tree.BaseTree;
import org.apache.wicket.markup.html.tree.LinkTree;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.syncope.client.mod.AttributeMod;
import org.syncope.client.mod.MembershipMod;
import org.syncope.client.mod.UserMod;
import org.syncope.client.to.AttributeTO;
import org.syncope.client.to.MembershipTO;
import org.syncope.client.to.RoleTO;
import org.syncope.client.to.UserTO;
import org.syncope.client.validation.SyncopeClientCompositeErrorException;
import org.syncope.console.commons.Constants;
import org.syncope.console.commons.RoleTreeBuilder;
import org.syncope.console.rest.RoleRestClient;
import org.syncope.console.rest.UserRestClient;
import org.syncope.console.wicket.ajax.markup.html.IndicatingDeleteOnConfirmAjaxLink;
import org.syncope.console.pages.panels.DerivedAttributesPanel;
import org.syncope.console.pages.panels.ResourcesPanel;
import org.syncope.console.pages.panels.VirtualAttributesPanel;

/**
 * Modal window with User form.
 */
public class UserModalPage extends BaseModalPage {

    @SpringBean
    private UserRestClient userRestClient;

    @SpringBean
    private RoleRestClient roleRestClient;

    @SpringBean
    private RoleTreeBuilder roleTreeBuilder;

    private WebMarkupContainer membershipsContainer;

    private AjaxButton submit;

    private List<MembershipTO> membershipTOs;

    private final ModalWindow membershipWin;

    private UserTO oldUser;

    private UserMod userMod;

    public UserModalPage(
            final Users basePage,
            final ModalWindow window,
            final UserTO userTO) {

        super();

        if (userTO.getId() > 0) {
            cloneOldUserTO(userTO);
        }

        membershipWin = new ModalWindow("membershipWin");
        membershipWin.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
        membershipWin.setPageMapName("create-membership-modal");
        membershipWin.setCookieName("create-membership-modal");
        add(membershipWin);

        add(new Label("id", String.valueOf(userTO.getId())));

        final Form form = new Form("UserForm");

        form.setModel(new CompoundPropertyModel(userTO));

        setupMemberships(userTO);

        //--------------------------------
        // Attributes panel
        //--------------------------------
        form.add(new AttributesPanel("attributes", userTO, form));

        final PasswordTextField password = new PasswordTextField("password");
        password.setRequired(userTO.getId() == 0);
        password.setResetPassword(true);
        form.add(password);

        final WebMarkupContainerWithAssociatedMarkup mandatoryPassword =
                new WebMarkupContainerWithAssociatedMarkup("mandatory_pwd");
        mandatoryPassword.add(new AbstractBehavior() {

            @Override
            public void onComponentTag(final Component component,
                    final ComponentTag tag) {

                if (userTO.getId() > 0) {
                    tag.put("style", "display:none;");
                }
            }
        });

        form.add(mandatoryPassword);
        //--------------------------------

        //--------------------------------
        // Derived attributes panel
        //--------------------------------
        form.add(new DerivedAttributesPanel("derivedAttributes", userTO));
        //--------------------------------

        //--------------------------------
        // Virtual attributes panel
        //--------------------------------
        form.add(new VirtualAttributesPanel("virtualAttributes", userTO));
        //--------------------------------

        //--------------------------------
        // Resources panel
        //--------------------------------
        form.add(new ResourcesPanel("resources", userTO));
        //--------------------------------

        submit = new IndicatingAjaxButton("submit", new Model(
                getString("submit"))) {

            @Override
            protected void onSubmit(final AjaxRequestTarget target,
                    final Form form) {

                final UserTO userTO = (UserTO) form.getModelObject();

                try {
                    userTO.setMemberships(getMembershipsSet());

                    if (userTO.getId() == 0) {
                        userRestClient.create(userTO);
                    } else {
                        setupUserMod(userTO);

                        //Update user just if it is changed
                        if (userMod != null) {
                            userRestClient.update(userMod);
                        }
                    }

                    basePage.setModalResult(true);
                    basePage.getPageParameters().put(
                            Constants.PAGEPARAM_CREATE, userTO.getId() == 0);
                    window.close(target);
                } catch (SyncopeClientCompositeErrorException e) {
                    LOG.error("While creating or updating user", e);
                    error(getString("error") + ":" + e.getMessage());
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form form) {
                target.addComponent(feedbackPanel);
            }
        };

        String allowedRoles = null;

        if (userTO.getId() == 0) {
            allowedRoles = xmlRolesReader.getAllAllowedRoles("Users", "create");
        } else {
            allowedRoles = xmlRolesReader.getAllAllowedRoles("Users", "update");
        }

        MetaDataRoleAuthorizationStrategy.authorize(
                submit, RENDER, allowedRoles);

        form.add(submit);

        // Roles Tab
        final List<RoleTO> roles = roleRestClient.getAllRoles();
        BaseTree tree = new LinkTree("treeTable",
                roleTreeBuilder.build(roles)) {

            @Override
            protected IModel getNodeTextModel(final IModel model) {
                return new PropertyModel(model, "userObject.displayName");
            }

            @Override
            protected void onNodeLinkClicked(final Object node,
                    final BaseTree tree, final AjaxRequestTarget target) {

                final RoleTO roleTO = (RoleTO) ((DefaultMutableTreeNode) node).getUserObject();

                membershipWin.setPageCreator(new ModalWindow.PageCreator() {

                    private MembershipTO membershipTO;

                    @Override
                    public Page createPage() {
                        membershipTO = new MembershipTO();
                        membershipTO.setRoleId(roleTO.getId());

                        return new MembershipModalPage(getPage(),
                                membershipWin, membershipTO, true);
                    }
                });
                membershipWin.show(target);
            }
        };

        tree.getTreeState().expandAll();
        tree.updateTree();

        form.add(tree);

        ListView membershipsView = new ListView("memberships", membershipTOs) {

            @Override
            protected void populateItem(final ListItem item) {
                final MembershipTO membershipTO =
                        (MembershipTO) item.getDefaultModelObject();

                item.add(new Label("roleId", new Model(
                        membershipTO.getRoleId())));
                item.add(new Label("roleName", new Model(
                        getRoleName(membershipTO.getRoleId(), roles))));

                AjaxLink editLink = new IndicatingAjaxLink("editLink") {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        membershipWin.setPageCreator(
                                new ModalWindow.PageCreator() {

                                    @Override
                                    public Page createPage() {
                                        MembershipModalPage window =
                                                new MembershipModalPage(
                                                getPage(), membershipWin,
                                                membershipTO,
                                                false);

                                        return window;

                                    }
                                });
                        membershipWin.show(target);
                    }
                };
                item.add(editLink);

                AjaxLink deleteLink = new IndicatingDeleteOnConfirmAjaxLink(
                        "deleteLink") {

                    @Override
                    public void onClick(final AjaxRequestTarget target) {
                        int componentId = new Integer(getParent().getId());
                        membershipTOs.remove(componentId);

                        target.addComponent(membershipsContainer);
                    }
                };
                item.add(deleteLink);
            }
        };

        membershipsContainer = new WebMarkupContainer("membershipsContainer");
        membershipsContainer.add(membershipsView);
        membershipsContainer.setOutputMarkupId(true);

        setWindowClosedCallback(membershipWin, membershipsContainer);

        form.add(membershipsContainer);
        add(form);
    }

    private String getRoleName(long roleId, List<RoleTO> roles) {
        boolean found = false;
        RoleTO roleTO;
        String result = null;
        for (Iterator<RoleTO> itor = roles.iterator();
                itor.hasNext() && !found;) {

            roleTO = itor.next();
            if (roleTO.getId() == roleId) {
                result = roleTO.getName();
            }
        }

        return result;
    }

    /**
     * Create a copy of old userTO object.
     * @param userTO the original TO
     */
    private void cloneOldUserTO(final UserTO userTO) {
        oldUser = new UserTO();

        oldUser.setId(userTO.getId());
        oldUser.setPassword(userTO.getPassword());

        AttributeTO attributeTO;
        for (AttributeTO attribute : userTO.getAttributes()) {
            attributeTO = new AttributeTO();
            attributeTO.setReadonly(attribute.isReadonly());

            attributeTO.setSchema(attribute.getSchema());

            for (String value : attribute.getValues()) {
                attributeTO.addValue(value);
            }

            oldUser.addAttribute(attributeTO);
        }

        for (AttributeTO attribute : userTO.getDerivedAttributes()) {
            attributeTO = new AttributeTO();
            attributeTO.setReadonly(attribute.isReadonly());

            attributeTO.setSchema(attribute.getSchema());

            for (String value : attribute.getValues()) {
                attributeTO.addValue(value);
            }

            oldUser.addDerivedAttribute(attributeTO);
        }

        for (String resource : userTO.getResources()) {
            oldUser.addResource(resource);
        }

        MembershipTO membership;

        for (MembershipTO membershipTO : userTO.getMemberships()) {
            membership = new MembershipTO();
            membership.setId(membershipTO.getId());
            membership.setRoleId(membershipTO.getRoleId());
            membership.setAttributes(membershipTO.getAttributes());
            oldUser.addMembership(membership);
        }
    }

    private void setWindowClosedCallback(final ModalWindow window,
            final WebMarkupContainer container) {

        window.setWindowClosedCallback(
                new ModalWindow.WindowClosedCallback() {

                    @Override
                    public void onClose(final AjaxRequestTarget target) {
                        target.addComponent(container);
                    }
                });
    }

    private void setupMemberships(final UserTO userTO) {
        membershipTOs = new ArrayList<MembershipTO>();

        if (userTO.getId() > 0) {
            List<MembershipTO> memberships = userTO.getMemberships();

            for (MembershipTO membership : memberships) {
                membershipTOs.add(membership);
            }
        }
    }

    /**
     * Convert a memberships ArrayList in a memberships HashSet list.
     * @return Set<MembershipTO> selected for a new user.
     */
    private List<MembershipTO> getMembershipsSet() {
        List<MembershipTO> memberships = new ArrayList<MembershipTO>();

        for (MembershipTO membership : membershipTOs) {
            memberships.add(membership);
        }

        return memberships;
    }

    public List<MembershipTO> getMembershipTOs() {
        return membershipTOs;
    }

    /**
     * Updates the modified user object.
     * @param userTO
     */
    private void setupUserMod(final UserTO userTO) {
        userMod = new UserMod();

        //1.Check if the password has been changed and update it
        if (oldUser.getPassword() != null
                && !oldUser.getPassword().equals(userTO.getPassword())) {
            userMod.setPassword(userTO.getPassword());
        }

        //2.Update user's schema derived attributes
        final List<AttributeTO> newDerivedAttributes =
                userTO.getDerivedAttributes();

        final List<AttributeTO> oldDerivedAttributes =
                oldUser.getDerivedAttributes();

        for (AttributeTO oldDerivedAttribute : oldDerivedAttributes) {
            userMod.addDerivedAttributeToBeRemoved(
                    oldDerivedAttribute.getSchema());
        }

        for (AttributeTO newDerivedAttribute : newDerivedAttributes) {
            userMod.addDerivedAttributeToBeAdded(
                    newDerivedAttribute.getSchema());
        }

        //3.Update user's schema virtual attributes
        final List<AttributeTO> newVirtualAttributes =
                userTO.getDerivedAttributes();

        final List<AttributeTO> oldVirtualAttributes =
                oldUser.getDerivedAttributes();

        for (AttributeTO oldVirtualAttribute : oldVirtualAttributes) {
            userMod.addVirtualAttributeToBeRemoved(
                    oldVirtualAttribute.getSchema());
        }

        for (AttributeTO newVirtualAttribute : newVirtualAttributes) {
            userMod.addVirtualAttributeToBeAdded(
                    newVirtualAttribute.getSchema());
        }

        //4.Update user's schema attributes
        for (AttributeTO attributeTO : userTO.getAttributes()) {
            searchAndUpdateAttribute(attributeTO);
        }

        //5.Update user's resources
        for (String resource : userTO.getResources()) {
            searchAndAddResource(resource);
        }


        for (String resource : oldUser.getResources()) {
            searchAndDropResource(resource, userTO);
        }

        //6.Update user's memberships
        for (MembershipTO membership : userTO.getMemberships()) {
            searchAndUpdateMembership(membership);
        }

        for (MembershipTO membership : oldUser.getMemberships()) {
            searchAndDropMembership(membership, userTO);
        }

        if (userMod != null) {
            userMod.setId(oldUser.getId());
        }
    }

    private void searchAndUpdateAttribute(AttributeTO attributeTO) {
        boolean found = false;
        boolean changed = false;

        AttributeMod attributeMod = new AttributeMod();
        attributeMod.setSchema(attributeTO.getSchema());

        for (AttributeTO oldAttribute : oldUser.getAttributes()) {
            if (attributeTO.getSchema().equals(oldAttribute.getSchema())) {

                if (!attributeTO.equals(oldAttribute) && !oldAttribute.isReadonly()) {

                    if (attributeTO.getValues().size() > 1) {
                        attributeMod.setValuesToBeAdded(
                                attributeTO.getValues());
                    } else {
                        attributeMod.addValueToBeAdded(
                                attributeTO.getValues().iterator().next());
                    }

                    if (userMod == null) {
                        userMod = new UserMod();
                    }

                    userMod.addAttributeToBeRemoved(oldAttribute.getSchema());
                    userMod.addAttributeToBeUpdated(attributeMod);

                    changed = true;
                    break;
                }
                found = true;
            }
        }

        if (!found & !changed && !attributeTO.isReadonly()
                && attributeTO.getValues() != null) {

            if (attributeTO.getValues().iterator().next() != null) {
                attributeMod.setValuesToBeAdded(attributeTO.getValues());

                if (userMod == null) {
                    userMod = new UserMod();
                }

                userMod.addAttributeToBeUpdated(attributeMod);
            } else {
                attributeMod = null;
            }
        }
    }

    /**
     * Search for a resource and add that one to the UserMod object if
     * it doesn't exist.
     * @param resource new resource added
     */
    private void searchAndAddResource(final String resource) {
        boolean found = false;

        /* Check if the current resource was existent before the update
        and in this        case just ignore it */
        for (String oldResource : oldUser.getResources()) {
            if (resource.equals(oldResource)) {
                found = true;
            }
        }

        if (!found) {
            if (userMod == null) {
                userMod = new UserMod();
            }

            userMod.addResourceToBeAdded(resource);
        }
    }

    /**
     * Search for a resource and drop that one from the UserMod object if
     * it doesn't exist anymore.
     * @param resource
     * @param userTO
     */
    private void searchAndDropResource(String resource, UserTO userTO) {
        boolean found = false;

        /*Check if the current resource was existing before the update
        and in this case just ignore it */
        for (String newResource : userTO.getResources()) {
            if (resource.equals(newResource)) {
                found = true;
            }
        }

        if (!found) {
            if (userMod == null) {
                userMod = new UserMod();
            }
            userMod.addResourceToBeRemoved(resource);
        }
    }

    /**
     * Update the Membership.
     * @param new membershipTO
     */
    private void searchAndUpdateMembership(MembershipTO membership) {
        boolean found = false;
        boolean attrFound = false;

        MembershipMod membershipMod = new MembershipMod();
        membershipMod.setRole(membership.getRoleId());

        AttributeMod attributeMod;

        //1. If the membership exists update it
        for (MembershipTO oldMembership : oldUser.getMemberships()) {
            if (membership.getRoleId() == oldMembership.getRoleId()) {

                for (AttributeTO newDerivedAttribute :
                        membership.getDerivedAttributes()) {
                    membershipMod.addDerivedAttributeToBeAdded(
                            newDerivedAttribute.getSchema());
                }

                for (AttributeTO newAttribute : membership.getAttributes()) {
                    for (AttributeTO oldAttribute :
                            oldMembership.getAttributes()) {

                        if (oldAttribute.getSchema().equals(
                                newAttribute.getSchema())) {

                            attributeMod = new AttributeMod();
                            attributeMod.setSchema(newAttribute.getSchema());

                            attributeMod.setValuesToBeAdded(
                                    newAttribute.getValues());

                            membershipMod.addAttributeToBeUpdated(attributeMod);
                            attrFound = true;
                            break;
                        }
                    }
                    if (!attrFound) {
                        attributeMod = new AttributeMod();
                        attributeMod.setSchema(newAttribute.getSchema());
                        attributeMod.setValuesToBeAdded(
                                newAttribute.getValues());
                        membershipMod.addAttributeToBeUpdated(attributeMod);
                    }
                    attrFound = false;
                }

                if (userMod == null) {
                    userMod = new UserMod();
                }

                userMod.addMembershipToBeRemoved(oldMembership.getId());
                userMod.addMembershipToBeAdded(membershipMod);

                found = true;
                break;
            }
        }

        //2.Otherwise, if it doesn't exist, create it from scratch
        if (!found) {
            Set<AttributeMod> attributes = new HashSet<AttributeMod>();

            for (AttributeTO newAttribute : membership.getAttributes()) {
                attributeMod = new AttributeMod();
                attributeMod.setSchema(newAttribute.getSchema());
                attributeMod.setValuesToBeAdded(newAttribute.getValues());

                attributes.add(attributeMod);
            }

            membershipMod.setAttributesToBeUpdated(attributes);

            if (userMod == null) {
                userMod = new UserMod();
            }

            userMod.addMembershipToBeAdded(membershipMod);
        }
    }

    /**
     * Drop membership not present anymore.
     * @param membershipTO
     * @param userTO
     */
    private void searchAndDropMembership(final MembershipTO oldMembership,
            final UserTO userTO) {

        boolean found = false;

        /*Check if the current resource was existent before the update and
        in this case just ignore it*/
        for (MembershipTO newMembership : userTO.getMemberships()) {
            if (newMembership.getId() == oldMembership.getId()) {
                found = true;
            }
        }

        if (!found) {
            if (userMod == null) {
                userMod = new UserMod();
            }

            userMod.addMembershipToBeRemoved(oldMembership.getId());
        }
    }
}
