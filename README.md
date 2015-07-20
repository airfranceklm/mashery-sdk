[![Build Status](https://travis-ci.org/afklm/mashery-sdk.svg?branch=master)](https://travis-ci.org/afklm/mashery-sdk)

# Mashery SDK
> SDK built on top of Intel-Mashery's V3 API.

##Usage

Creating a Mashery Client

```java
public Map<String, String> oauthRequestParams = new HashMap<String, String>();

oauthRequestParams.put("grant_type", "password");
oauthRequestParams.put("username", "fakeMasheryUserName");
oauthRequestParams.put("password", "fakeMasheryPassword");
oauthRequestParams.put("scope", "fakeMasheryUUID");
			
public MasheryClient client = MasheryClientBuilder.masheryClient()
				.withHost("api.mashery.com")
				.withProtocol("https")
				.withPort(443)
				.withApiKey("fakeApiKey")
				.withApiSecret("fakeApiSecret").
				withTokenRequestParams(oauthRequestParams)
				.build();
```

> More coming soon...

## TODO
- Add Support for Error Sets
- Add Access Controls (V2 API only?)
- Add Plan Method support.

## License
This module is released under the permissive [MIT license](LICENSE).

Contributions are welcome.
