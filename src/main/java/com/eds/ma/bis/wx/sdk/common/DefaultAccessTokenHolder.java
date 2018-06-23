package com.eds.ma.bis.wx.sdk.common;

/**
 * @borball on 8/14/2016.
 */
public class DefaultAccessTokenHolder extends AccessTokenHolder {

    private static DefaultAccessTokenHolder accessTokenHolder;

    public static DefaultAccessTokenHolder with(String tokenUrl) {
        if(accessTokenHolder == null){
            accessTokenHolder = new DefaultAccessTokenHolder(tokenUrl);
        }
        return accessTokenHolder;
    }

    private DefaultAccessTokenHolder(String tokenUrl){
        super(tokenUrl);
    }

    @Override
    public synchronized AccessToken getAccessToken() {
        String content = fetchAccessToken();
        return AccessToken.fromJson(content);
    }

}
