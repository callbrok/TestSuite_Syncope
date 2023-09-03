package org.apache.syncope.core.spring.security.util;

import org.apache.syncope.core.persistence.api.entity.*;
import org.apache.syncope.core.persistence.api.entity.policy.*;

import java.util.List;
import java.util.Optional;

public class RealmImpl implements Realm {

    private PasswordPolicy policy;

    public RealmImpl(PasswordPolicy policy){setPasswordPolicy(policy);}

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public Realm getParent() {
        return null;
    }

    @Override
    public void setParent(Realm parent) {

    }

    @Override
    public String getFullPath() {
        return null;
    }

    @Override
    public AccountPolicy getAccountPolicy() {
        return null;
    }

    @Override
    public void setAccountPolicy(AccountPolicy accountPolicy) {

    }

    @Override
    public PasswordPolicy getPasswordPolicy() {
        return this.policy;
    }

    @Override
    public void setPasswordPolicy(PasswordPolicy passwordPolicy) {
        this.policy = passwordPolicy;
    }

    @Override
    public AuthPolicy getAuthPolicy() {
        return null;
    }

    @Override
    public void setAuthPolicy(AuthPolicy authPolicy) {

    }

    @Override
    public AccessPolicy getAccessPolicy() {
        return null;
    }

    @Override
    public void setAccessPolicy(AccessPolicy accessPolicy) {

    }

    @Override
    public AttrReleasePolicy getAttrReleasePolicy() {
        return null;
    }

    @Override
    public void setAttrReleasePolicy(AttrReleasePolicy policy) {

    }

    @Override
    public TicketExpirationPolicy getTicketExpirationPolicy() {
        return null;
    }

    @Override
    public void setTicketExpirationPolicy(TicketExpirationPolicy policy) {

    }

    @Override
    public boolean add(Implementation action) {
        return false;
    }

    @Override
    public List<? extends Implementation> getActions() {
        return null;
    }

    @Override
    public boolean add(AnyTemplateRealm template) {
        return false;
    }

    @Override
    public Optional<? extends AnyTemplateRealm> getTemplate(AnyType anyType) {
        return Optional.empty();
    }

    @Override
    public List<? extends AnyTemplateRealm> getTemplates() {
        return null;
    }

    @Override
    public boolean add(ExternalResource resource) {
        return false;
    }

    @Override
    public List<String> getResourceKeys() {
        return null;
    }

    @Override
    public List<? extends ExternalResource> getResources() {
        return null;
    }
}
