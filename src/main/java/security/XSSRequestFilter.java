package security;

import jakarta.annotation.Priority;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.ext.Provider;
import org.apache.commons.collections.CollectionUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.owasp.esapi.ESAPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Code by Alessandro Giannone used as reference:
 * https://codehustler.org/blog/jersey-cross-site-scripting-xss-filter-for-java-web-apps/
 */

@Provider
@PreMatching
@Priority(1)
public class XSSRequestFilter implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) {
        cleanQueryParams(requestContext);
        cleanHeaders(requestContext.getHeaders());
    }

    private void cleanQueryParams(ContainerRequestContext requestContext) {
        UriBuilder builder = requestContext.getUriInfo().getRequestUriBuilder();
        MultivaluedMap<String, String> queries = requestContext.getUriInfo().getQueryParameters();

        for(Map.Entry<String, List<String>> query : queries.entrySet())
        {
            String key = query.getKey();
            List<String> values = query.getValue();

            List<String> xssValues = new ArrayList<>();
            for(String value : values) {
                xssValues.add(removeXXS(value));
            }

            int size = CollectionUtils.size(xssValues);
            builder.replaceQueryParam(key);

            if(size == 1) {
                builder.replaceQueryParam(key, xssValues.get(0));
            } else if(size > 1) {
                builder.replaceQueryParam(key, xssValues.toArray());
            }
        }

    }

    private void cleanHeaders (MultivaluedMap<String, String> headers) {
        for(Map.Entry<String, List<String>> header : headers.entrySet())
        {
            String key = header.getKey();
            List<String> values = header.getValue();

            List<String> cleanValues = new ArrayList<String>();
            for(String value : values) {
                cleanValues.add(removeXXS(value));
            }

            headers.put(key, cleanValues);
        }
    }

    public String removeXXS(String value) {
        if( value == null )
            return null;

        // Use the ESAPI library to avoid double encoded attacks.
        // decodes HTMLEntity, percent (URL) encoding, and JavaScript encoding
        value = ESAPI.encoder().canonicalize(value);

        // Avoid null characters
        value = value.replaceAll("\0", " ");

        // Clean out HTML
        Document.OutputSettings outputSettings = new Document.OutputSettings();
        outputSettings.escapeMode(Entities.EscapeMode.xhtml);
        outputSettings.prettyPrint(false);
        value = Jsoup.clean(value, "", Safelist.basic(), outputSettings);

        return value;
    }

}
