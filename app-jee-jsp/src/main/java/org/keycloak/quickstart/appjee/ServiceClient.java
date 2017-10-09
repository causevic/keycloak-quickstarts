/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.quickstart.appjee;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.util.JsonSerialization;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.List;

/**
 * Client that calls the service.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2015 Red Hat Inc.
 */
public class ServiceClient {

   
public static class ProductResponse 
{
    private List<String> products;

    public ProductResponse(List<String> productList) {
        products = productList;
    }

    public ProductResponse()
    {}
	/**
	 * @return the products
	 */
	public List<String> getProducts() {
		return products;
	}

	/**
	 * @param products the products to set
	 */
	public void setProducts(List<String> products) {
		this.products = products;
	}
}

    public static class Failure extends Exception {
        private int status;
        private String reason;

        public Failure(int status, String reason) {
            this.status = status;
            this.reason = reason;
        }

        public int getStatus() {
            return status;
        }

        public String getReason() {
            return reason;
        }
    }

    private static String getServiceUrl(HttpServletRequest req) {
        return ServiceLocator.getServiceUrl(req).toExternalForm();
    }

    public static CloseableHttpClient createHttpClient() {
        return HttpClients.createDefault();
    }

    public static String callService(HttpServletRequest req, KeycloakSecurityContext session, String action) throws Failure {
        CloseableHttpClient client = null;
        try {
            client = createHttpClient();
            String getUri = getServiceUrl(req) + "/" + action;
            System.out.println("# # # # # # # # # # Get Service URI: " + getUri);
            HttpGet get = new HttpGet(getUri);
            if (session != null) {
                get.addHeader("Authorization", "Bearer " + session.getTokenString());
            }

            HttpResponse response = client.execute(get);

            StatusLine status = response.getStatusLine();
            if (status.getStatusCode() != 200) {
                throw new Failure(status.getStatusCode(), status.getReasonPhrase());
            }

            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            try {
                ProductResponse message = JsonSerialization.readValue(is, ProductResponse.class);
                return message.getProducts().get(0);
            } finally {
                is.close();
            }
        } catch (Failure f) {
            throw f;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (client != null)
                try {
                    client.close();
                } catch (Exception e) {
                    throw new RuntimeException("Error while closing HttpClient", e);
                }
        }
    }
}
