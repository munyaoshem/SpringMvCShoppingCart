package org.shoppingcart.model;

import java.util.ArrayList;
import java.util.List;

public class CartInfo {

	private int orderNum;
	private CustomerInfo customerInfo;
	private final List<CartLineInfo> cartLines = new ArrayList<CartLineInfo>();

	public CartInfo() {

	}

	/**
	 * @return the orderNum
	 */
	public int getOrderNum() {
		return orderNum;
	}

	/**
	 * @param orderNum the orderNum to set
	 */
	public void setOrderNum(int orderNum) {
		this.orderNum = orderNum;
	}

	/**
	 * @return the customerInfo
	 */
	public CustomerInfo getCustomerInfo() {
		return customerInfo;
	}

	/**
	 * @param customerInfo the customerInfo to set
	 */
	public void setCustomerInfo(CustomerInfo customerInfo) {
		this.customerInfo = customerInfo;
	}

	/**
	 * @return the cartLines
	 */
	public List<CartLineInfo> getCartLines() {
		return cartLines;
	}

	/**
	 * 
	 * @param code
	 * @return
	 */
	private CartLineInfo findLineByCode(String code) {
		for (CartLineInfo line : this.cartLines) {
			if (line.getProductInfo().getCode().equals(code)) {
				return line;
			}
		}
		return null;
	}

	public void addProduct(ProductInfo productInfo, int quantity) {
		CartLineInfo lineInfo = this.findLineByCode(productInfo.getCode());

		if (lineInfo == null) {
			lineInfo = new CartLineInfo();
			lineInfo.setQuantity(0);
			lineInfo.setProductInfo(productInfo);
			this.cartLines.add(lineInfo);
		}

		int newQuantity = lineInfo.getQuantity() + quantity;

		if (newQuantity <= 0) {
			this.cartLines.remove(lineInfo);
		} else {
			lineInfo.setQuantity(newQuantity);
		}
	}

	public void validate() {

	}

	public void updateProduct(String code, int quantity) {
		CartLineInfo lineInfo = this.findLineByCode(code);

		if (lineInfo != null) {
			if (quantity <= 0) {
				this.cartLines.remove(lineInfo);
			} else {
				lineInfo.setQuantity(quantity);
			}
		}
	}

	public void removeProduct(ProductInfo productInfo) {
		CartLineInfo lineInfo = this.findLineByCode(productInfo.getCode());
		if (lineInfo != null) {
			this.cartLines.remove(lineInfo);
		}
	}

	public boolean isEmpty() {
		return this.cartLines.isEmpty();
	}

	public boolean isValidCustomer() {
		return this.customerInfo != null && this.customerInfo.isValid();
	}

	public int getQuantityTotal() {
		int quantity = 0;
		for (CartLineInfo lineInfo : this.cartLines) {
			quantity += lineInfo.getQuantity();
		}
		return quantity;
	}

	public double getAmountTotal() {
		double total = 0;
		for (CartLineInfo lineInfo : this.cartLines) {
			total += lineInfo.getAmount();
		}
		return total;
	}

	public void updateQuantity(CartInfo cartInfo) {
		if (cartInfo != null) {
			List<CartLineInfo> cartLineInfos = cartInfo.getCartLines();
			for (CartLineInfo lineInfo : cartLineInfos) {
				this.updateProduct(lineInfo.getProductInfo().getCode(), lineInfo.getQuantity());
			}
		}
	}
}