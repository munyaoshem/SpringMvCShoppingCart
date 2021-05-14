package org.shoppingcart.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.shoppingcart.dao.OrderDAO;
import org.shoppingcart.dao.ProductDAO;
import org.shoppingcart.model.OrderDetailInfo;
import org.shoppingcart.model.OrderInfo;
import org.shoppingcart.model.PaginationResult;
import org.shoppingcart.model.ProductInfo;
import org.shoppingcart.validator.ProductInfoValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
//Enable Hibernate Transaction.
@Transactional
//Need to use RedirectAttributes
@EnableWebMvc
public class AdminController {

	private static final Logger LOGEVENT = Logger.getLogger(AdminController.class);

	@Autowired
	private OrderDAO orderDAO;

	@Autowired
	private ProductDAO productDAO;

	@Autowired
	private ProductInfoValidator productInfoValidator;

//	// Configured in ApplicationContextConfig
//	@Autowired
//	private ResourceBundleMessageSource messageSource;

	@InitBinder
	public void myInitBinder(WebDataBinder dataBinder) {
		Object target = dataBinder.getTarget();

		if (target == null) {
			return;
		}

		System.out.println("Targer=" + target);

		if (target.getClass() == ProductInfo.class) {
			dataBinder.setValidator(productInfoValidator);

			// For Upload Image
			dataBinder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
		}
	}

	// GET: Show Login Page
	@RequestMapping(value = { "/login" }, method = RequestMethod.GET)
	public String login(Model model) {
		return "login";
	}

	@RequestMapping(value = { "/accountInfo" }, method = RequestMethod.GET)
	public String accountInfo(Model model) {

		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		LOGEVENT.info("Username: " + userDetails.getUsername());
		LOGEVENT.info("Password: " + userDetails.getPassword());
		LOGEVENT.info("IsEnabled: " + userDetails.isEnabled());

		model.addAttribute("userDetails", userDetails);
		return "accountInfo";
	}

	@RequestMapping(value = { "/orderList" }, method = RequestMethod.GET)
	public String orderList(Model model, @RequestParam(value = "page", defaultValue = "1") String pageStr) {
		int page = 1;

		try {
			page = Integer.parseInt(pageStr);
		} catch (Exception e) {
		}

		final int MAX_RESULT = 5;
		final int MAX_NAVIGATION_PAGE = 10;

		PaginationResult<OrderInfo> paginationResult = orderDAO.listOrderInfo(page, MAX_RESULT, MAX_NAVIGATION_PAGE);

		model.addAttribute("paginationResult", paginationResult);
		return "orderList";
	}

	// GET: Show product
	@RequestMapping(value = { "/product" }, method = RequestMethod.GET)
	public String product(Model model, @RequestParam(value = "code", defaultValue = "") String code) {

		ProductInfo productInfo = null;

		if (code != null && code.length() > 0) {
			productInfo = productDAO.findProductInfo(code);
		}

		if (productInfo == null) {
			productInfo = new ProductInfo();
			productInfo.setNewProduct(true);
		}

		model.addAttribute("productForm", productInfo);
		return "product";
	}

	// POST: Save product
	@RequestMapping(value = { "/product" }, method = RequestMethod.POST)
	// Avoid UnexpectedRollbackException
	@Transactional(propagation = Propagation.NEVER)
	public String productSave(Model model, @ModelAttribute("productForm") @Validated ProductInfo productInfo,
			BindingResult result, final RedirectAttributes redirectAttributes) {

		if (result.hasErrors()) {
			return "product";
		}

		try {
			productDAO.save(productInfo);
		} catch (Exception e) {
			// Need: Propagation.NEVER?
			String message = e.getMessage();
			model.addAttribute("message", message);
			// Show product form
			return "product";
		}

		return "redirect:/productList";
	}

	@RequestMapping(value = { "/order" }, method = RequestMethod.GET)
	public String orderView(Model model, @RequestParam("orderId") String orderId) {
		OrderInfo orderInfo = null;

		if (orderId != null) {
			orderInfo = this.orderDAO.getOrderInfo(orderId);
		}

		if (orderInfo == null) {
			return "redirect:/orderList";
		}

		List<OrderDetailInfo> details = this.orderDAO.listOrderDetailInfo(orderId);
		orderInfo.setDetails(details);

		model.addAttribute("orderInfo", orderInfo);

		return "order";
	}
}