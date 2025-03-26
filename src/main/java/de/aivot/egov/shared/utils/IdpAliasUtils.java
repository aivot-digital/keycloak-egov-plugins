package de.aivot.egov.shared.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URI;
import java.util.regex.Pattern;

public class IdpAliasUtils {
    private final static Pattern pattern = Pattern.compile("/realms/.+/broker/(.+)/endpoint");

    @Nullable
    public static String extractIdpAliasFromSenderUrl(@Nonnull URI senderURI) {
        var matcher = pattern.matcher(senderURI.getPath());
        String idpAlias = null;
        if (matcher.find()) {
            idpAlias = matcher.group(1);
        }
        return idpAlias;
    }
}
