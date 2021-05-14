package org.shoppingcart.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.query.Query;

public class PaginationResult<E> {

	private int totalRecords;
	private int currentPage;
	private List<E> list;
	private int maxResult;
	private int totalPages;
	private int maxNavigationPage;
	private List<Integer> navigationPages;

	// @page: 1,2, ..
	@SuppressWarnings("unchecked")
	public PaginationResult(Query<E> query, int page, int maxResult, int maxNavigationPage) {
		final int pageIndex = page - 1 < 0 ? 0 : page - 1;

		int fromRecordIndex = pageIndex * maxResult;
		int maxRecordIndex = fromRecordIndex + maxResult;

		ScrollableResults resultScroll = query.scroll(ScrollMode.SCROLL_INSENSITIVE);

		List<E> results = new ArrayList<E>();
		boolean hasResult = resultScroll.first();

		if (hasResult) {
			// Scroll to position
			hasResult = resultScroll.scroll(fromRecordIndex);

			if (hasResult) {
				do {
					E record = (E) resultScroll.get(0);
					results.add(record);
				} while (resultScroll.next() && resultScroll.getRowNumber() >= fromRecordIndex
						&& resultScroll.getRowNumber() < maxRecordIndex);
				{

				}

				// Go to last record
				resultScroll.last();
			}

			// Total Records
			this.totalRecords = resultScroll.getRowNumber() + 1;
			this.currentPage = pageIndex + 1;
			this.list = results;
			this.maxResult = maxResult;
			this.totalPages = (this.totalRecords / this.maxResult) + 1;
			this.maxNavigationPage = maxNavigationPage;

			if (maxNavigationPage < totalPages) {
				this.maxNavigationPage = maxNavigationPage;
			}

			this.calcNavigationPages();
		}
	}

	private void calcNavigationPages() {
		this.navigationPages = new ArrayList<Integer>();

		int current = this.currentPage > this.totalPages ? this.totalPages : this.currentPage;

		int begin = current - this.maxNavigationPage / 2;
		int end = current + this.maxNavigationPage / 2;

		// First page
		navigationPages.add(1);

		if (begin > 2) {
			// For '...'
			navigationPages.add(-1);
		}

		for (int i = begin; i < end; i++) {
			if (i > 1 && i < this.totalPages) {
				navigationPages.add(i);
			}
		}

		if (end < this.totalPages - 2) {
			// For '...'
			navigationPages.add(-1);
		}

		// Last Page
		navigationPages.add(this.totalPages);
	}

	/**
	 * @return the totalRecords
	 */
	public int getTotalRecords() {
		return totalRecords;
	}

	/**
	 * @param totalRecords the totalRecords to set
	 */
	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	/**
	 * @return the currentPage
	 */
	public int getCurrentPage() {
		return currentPage;
	}

	/**
	 * @param currentPage the currentPage to set
	 */
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	/**
	 * @return the list
	 */
	public List<E> getList() {
		return list;
	}

	/**
	 * @param list the list to set
	 */
	public void setList(List<E> list) {
		this.list = list;
	}

	/**
	 * @return the maxResult
	 */
	public int getMaxResult() {
		return maxResult;
	}

	/**
	 * @param maxResult the maxResult to set
	 */
	public void setMaxResult(int maxResult) {
		this.maxResult = maxResult;
	}

	/**
	 * @return the totalPages
	 */
	public int getTotalPages() {
		return totalPages;
	}

	/**
	 * @param totalPages the totalPages to set
	 */
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	/**
	 * @return the maxNavigationPage
	 */
	public int getMaxNavigationPage() {
		return maxNavigationPage;
	}

	/**
	 * @param maxNavigationPage the maxNavigationPage to set
	 */
	public void setMaxNavigationPage(int maxNavigationPage) {
		this.maxNavigationPage = maxNavigationPage;
	}

	/**
	 * @return the navigationPages
	 */
	public List<Integer> getNavigationPages() {
		return navigationPages;
	}

	/**
	 * @param navigationPages the navigationPages to set
	 */
	public void setNavigationPages(List<Integer> navigationPages) {
		this.navigationPages = navigationPages;
	}
}