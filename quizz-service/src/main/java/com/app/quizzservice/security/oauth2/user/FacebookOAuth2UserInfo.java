package com.app.quizzservice.security.oauth2.user;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class FacebookOAuth2UserInfo extends OAuth2UserInfo {
    public FacebookOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getFirstName() {
        return attributes.getOrDefault("first_name", getName()).toString();
    }

    @Override
    public String getLastName() {
        return attributes.getOrDefault("last_name", getName()).toString();
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        if (attributes.containsKey("picture") && attributes.get("picture") instanceof Map<?, ?> pictureObj) {
            if (pictureObj.containsKey("data") && pictureObj.get("data") instanceof Map<?, ?> dataObj) {
                if (dataObj.containsKey("url") && dataObj.get("url") instanceof String url) {
                    return url;
                }
            }
        }
        return StringUtils.EMPTY;
    }
}
