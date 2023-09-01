package org.apache.syncope.core.spring.security.util;

import org.apache.syncope.common.lib.types.IdRepoImplementationType;
import org.apache.syncope.common.lib.types.ImplementationEngine;
import org.apache.syncope.core.persistence.api.entity.Implementation;

/** Manage rule to add to the policy */

public class ImplementationImpl implements Implementation {
    String body;

    public ImplementationImpl(String body) {setBody(body);}

    @Override
    public String getKey() {return "";}

    @Override
    public ImplementationEngine getEngine() {
        return ImplementationEngine.JAVA;
    }

    @Override
    public void setEngine(ImplementationEngine engine) {}

    @Override
    public String getType() {
        return IdRepoImplementationType.PASSWORD_RULE;
    }

    @Override
    public void setType(String type) {}

    @Override
    public String getBody() {
        return body;
    }

    @Override
    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public void setKey(String key) {

    }
}
