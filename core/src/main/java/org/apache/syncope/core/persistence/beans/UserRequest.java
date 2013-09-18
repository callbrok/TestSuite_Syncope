/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.syncope.core.persistence.beans;

import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.syncope.common.mod.UserMod;
import org.apache.syncope.common.to.UserTO;
import org.apache.syncope.common.types.UserRequestType;
import org.apache.syncope.core.util.XMLSerializer;

@Entity
public class UserRequest extends AbstractBaseBean {

    private static final long serialVersionUID = 4977358381988835119L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String username;

    @NotNull
    @Lob
    private String payload;

    @NotNull
    @Enumerated(EnumType.STRING)
    private UserRequestType type;

    @Basic
    @Min(0)
    @Max(1)
    private Integer executed;

    /**
     * Creation date.
     */
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    /**
     * Claim date.
     */
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date claimDate;

    /**
     * Execution date.
     */
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date executionDate;

    /**
     * Username of the last user who claimed the request.
     */
    private String owner;

    public UserRequest() {
        this.executed = 0;
    }

    public Long getId() {
        return id;
    }

    public UserRequestType getType() {
        return type;
    }

    public UserTO getUserTO() {
        return type != UserRequestType.CREATE
                ? null
                : XMLSerializer.<UserTO>deserialize(payload);
    }

    public void createUser(final UserTO userTO) {
        type = UserRequestType.CREATE;
        payload = XMLSerializer.serialize(userTO);
    }

    public UserMod getUserMod() {
        return type != UserRequestType.UPDATE
                ? null
                : XMLSerializer.<UserMod>deserialize(payload);
    }

    public void updateUser(final UserMod userMod) {
        type = UserRequestType.UPDATE;
        payload = XMLSerializer.serialize(userMod);
    }

    public Long getUserId() {
        return type != UserRequestType.DELETE
                ? null
                : Long.valueOf(payload);
    }

    public void deleteUser(final Long userId) {
        type = UserRequestType.DELETE;
        payload = String.valueOf(userId);
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public boolean isExecuted() {
        return isBooleanAsInteger(executed);
    }

    public void setExecuted(boolean executed) {
        this.executed = getBooleanAsInteger(executed);
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getClaimDate() {
        return claimDate;
    }

    public void setClaimDate(Date claimDate) {
        this.claimDate = claimDate;
    }

    public Date getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(Date executionDate) {
        this.executionDate = executionDate;
    }
}
