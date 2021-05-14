package org.shoppingcart.model;

import org.shoppingcart.entity.Product;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class ProductInfo {

	private String code;
	private String name;
	private double price;
	private boolean newProduct = false;

	// Upload file
	private CommonsMultipartFile multipartFile;

	public ProductInfo() {
		// No arg constructor
	}

	public ProductInfo(Product product) {
		this.code = product.getCode();
		this.name = product.getName();
		this.price = product.getPrice();
	}

	public ProductInfo(String code, String name, double price) {
		this.code = code;
		this.name = name;
		this.price = price;
	}

    public ProductInfo(String code, String name, double price, int quantity_available) {
    }

    /**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the price
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(double price) {
		this.price = price;
	}

	/**
	 * @return the newProduct
	 */
	public boolean isNewProduct() {
		return newProduct;
	}

	/**
	 * @param newProduct the newProduct to set
	 */
	public void setNewProduct(boolean newProduct) {
		this.newProduct = newProduct;
	}

	/**
	 * @return the multipartFile
	 */
	public CommonsMultipartFile getMultipartFile() {
		return multipartFile;
	}

	/**
	 * @param multipartFile the multipartFile to set
	 */
	public void setMultipartFile(CommonsMultipartFile multipartFile) {
		this.multipartFile = multipartFile;
	}

	public int getQuantity_available() {
		int quantity_available = 0;
		return  quantity_available;
	}
}