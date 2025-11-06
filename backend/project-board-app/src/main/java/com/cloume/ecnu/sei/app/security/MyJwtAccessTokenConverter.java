package com.cloume.ecnu.sei.app.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.Map;

/**
 * @author yxiao
 */
@Configuration
public class MyJwtAccessTokenConverter extends JwtAccessTokenConverter {

	@Value("${security.oauth2.resource.jwt.key-value:xxxx}")
	private String signingKey;

	public MyJwtAccessTokenConverter() {
		super.setSigningKey("xxx");
	}

	@Override
	protected Map<String, Object> decode(String token) {
		try {
			return super.decode(token);
		}
		catch (Exception e) {
			throw new InvalidTokenException("Cannot convert access token to JSON", e);
		}
	}

}
