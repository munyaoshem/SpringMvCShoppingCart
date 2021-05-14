package org.shoppingcart.dao.impl;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.shoppingcart.dao.ProductDAO;
import org.shoppingcart.entity.Product;
import org.shoppingcart.model.PaginationResult;
import org.shoppingcart.model.ProductInfo;
import org.springframework.beans.factory.annotation.Autowired;

//Transactional for Hibernate
@Transactional
public class ProductDAOImpl implements ProductDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@SuppressWarnings("deprecation")
	@Override
	public Product findProduct(String code) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(Product.class);
		criteria.add(Restrictions.eq("code", code));
		return (Product) criteria.uniqueResult();
	}

	@Override
	public ProductInfo findProductInfo(String code) {
		Product product = this.findProduct(code);
		if (product == null) {
			return null;
		}
		return new ProductInfo(product.getCode(), product.getName(), product.getPrice(), product.getQuantity_available());
	}
	
	@Override
	public void save(ProductInfo productInfo) {
		String code = productInfo.getCode();
		Product product = null;

		boolean isNew = false;
		if (code != null) {
			product = this.findProduct(code);
		}

		if (product == null) {
			isNew = true;
			product = new Product();
		}

		product.setCode(code);
		product.setName(productInfo.getName());
		product.setPrice(productInfo.getPrice());
		product.setQuantity_available(productInfo.getQuantity_available());

		if (productInfo.getMultipartFile() != null) {
			byte[] image = productInfo.getMultipartFile().getBytes();
			if (image != null && image.length > 0) {
				product.setImage(image);
			}
		}

		if (isNew) {
			try {
				this.sessionFactory.getCurrentSession().persist(product);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// If error in DB then Exceptions will be thrown out immediately
		this.sessionFactory.getCurrentSession().flush();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public PaginationResult<ProductInfo> queryProducts(int page, int maxResult, int maxNavigationPage,
			String likeName) {
		String sql = "Select new " + ProductInfo.class.getName() + " (prod.code, prod.name, prod.price) from "
				+ Product.class.getName() + " prod ";
		if (likeName != null && likeName.length() > 0) {
			sql += "Where lower(prod.name) like: likeName ";
		}
		sql += "order by prod.createDate desc";

		Session session = sessionFactory.getCurrentSession();

		Query query = session.createQuery(sql);
		if (likeName != null && likeName.length() > 0) {
			query.setParameter("likeName", "%" + likeName.toLowerCase() + "%");
		}
		return new PaginationResult<ProductInfo>(query, page, maxResult, maxNavigationPage);
	}
	
	@Override
	public PaginationResult<ProductInfo> queryProducts(int page, int maxResult, int maxNavigationPage) {
		return queryProducts(page, maxResult, maxNavigationPage, null);
	}
}