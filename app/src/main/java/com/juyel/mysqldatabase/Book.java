package com.juyel.mysqldatabase;

public class Book {
	
	private String title;
	private String author;
	private String isbn;
	private String category;
	private double price;
	
	public Book(String title, String author, String isbn, String category,
			double price) {
		super();
		this.title = title;
		this.author = author;
		this.isbn = isbn;
		this.category = category;
		this.price = price;
	}

	public Book() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "Book [title=" + title + ", author=" + author + ", isbn=" + isbn
				+ ", category=" + category + ", price=" + price + "]";
	}
	
	

}
