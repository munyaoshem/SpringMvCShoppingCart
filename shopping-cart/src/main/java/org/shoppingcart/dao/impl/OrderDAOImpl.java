package org.shoppingcart.dao.impl;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.shoppingcart.dao.OrderDAO;
import org.shoppingcart.dao.ProductDAO;
import org.shoppingcart.entity.Order;
import org.shoppingcart.entity.OrderDetail;
import org.shoppingcart.entity.Product;
import org.shoppingcart.model.CartInfo;
import org.shoppingcart.model.CartLineInfo;
import org.shoppingcart.model.CustomerInfo;
import org.shoppingcart.model.OrderDetailInfo;
import org.shoppingcart.model.OrderInfo;
import org.shoppingcart.model.PaginationResult;
import org.springframework.beans.factory.annotation.Autowired;

//Transactional for Hibernate
@Transactional
public class OrderDAOImpl implements OrderDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private ProductDAO productDAO;

	@SuppressWarnings("rawtypes")
	private int getMaxOrderNum() {
		String sql = "Select max(ord.orderNum) from " + Order.class.getName() + " ord";
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(sql);
		Integer value = (Integer) query.uniqueResult();

		if (value == null) {
			return 0;
		}

		return value;
	}

	@Override
	public void saveOrder(CartInfo cartInfo) {
		Session session = sessionFactory.getCurrentSession();

		int orderNum = this.getMaxOrderNum() + 1;
		Order order = new Order();

		order.setOrderNum(orderNum);
		order.setOrderDate(new Date());
		order.setAmount(cartInfo.getAmountTotal());

		CustomerInfo customerInfo = cartInfo.getCustomerInfo();
		order.setCustomerName(customerInfo.getName());
		order.setCustomerEmail(customerInfo.getEmail());
		order.setCustomerPhone(customerInfo.getPhone());
		order.setCustomerAddress(customerInfo.getAddress());

		session.persist(order);

		List<CartLineInfo> cartLines = cartInfo.getCartLines();

		for (CartLineInfo lineInfo : cartLines) {
			OrderDetail orderDetail = new OrderDetail();
			orderDetail.setOrder(order);
			orderDetail.setAmount(lineInfo.getAmount());
			orderDetail.setPrice(lineInfo.getProductInfo().getPrice());
			orderDetail.setQuantity(lineInfo.getQuantity());

			String code = lineInfo.getProductInfo().getCode();
			Product product = this.productDAO.findProduct(code);
			orderDetail.setProduct(product);

			session.persist(orderDetail);
		}

		// Set OrderNum for report.

		cartInfo.setOrderNum(orderNum);
	}

	// @page = 1,2,..
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public PaginationResult<OrderInfo> listOrderInfo(int page, int maxResult, int maxNivagationPage) {
		String sql = "Select new " + OrderInfo.class.getName()
				+ " (ord.orderId, ord.orderDate, ord.orderNum, ord.amount, ord.customerName, ord.customerAddress, ord.customerEmail, ord.customerPhone) from "
				+ Order.class.getName() + " ord order by ord.orderNum desc";
		Session session = this.sessionFactory.getCurrentSession();

		Query query = session.createQuery(sql);

		return new PaginationResult<OrderInfo>(query, page, maxResult, maxNivagationPage);
	}

	@SuppressWarnings("deprecation")
	public Order findOrder(String orderId) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(Order.class);
		criteria.add(Restrictions.eq("id", orderId));
		return (Order) criteria.uniqueResult();
	}

	@Override
	public OrderInfo getOrderInfo(String orderId) {
		Order order = this.findOrder(orderId);
		if (order == null) {
			return null;
		}
		return new OrderInfo(Integer.toString(order.getOrderId()), order.getOrderDate(), order.getOrderNum(),
				order.getAmount(), order.getCustomerName(), order.getCustomerAddress(), order.getCustomerEmail(),
				order.getCustomerPhone());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<OrderDetailInfo> listOrderDetailInfo(String orderId) {
		String sql = "Select new " + OrderDetailInfo.class.getName()
				+ " (ord.id, ord.product.code, ord.product.name, ord.quantity, ord.price, ord.amount) from "
				+ OrderDetail.class.getName() + " ord where ord.order.orderId=:orderId";

		Session session = this.sessionFactory.getCurrentSession();
		Query query = session.createQuery(sql);
		query.setParameter("orderId", orderId);
		return query.list();
	}

}