package org.keycloak.quickstart.springboot.model;

import java.util.List;


public class ProductResponse 
{
    private List<String> products;

    public ProductResponse(List<String> productList) {
        products = productList;
    }

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