package org.chaby.GridPaginatiopnDemo;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.FooterRow.FooterCell;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToBigDecimalConverter;
import com.vaadin.flow.data.converter.StringToBooleanConverter;
import com.vaadin.flow.data.converter.StringToFloatConverter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.value.ValueChangeMode;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PaginationGrid<T, F> extends Grid<T> {

	private int pageNumber = 0;
	private int totalElements;
	private Label pageLabel;
	private Button prev;
	private Button next;

	@Getter
	@Setter
	private F filter;
	private Class<F> filterType;
	private Binder<F> binder;

	@Getter
	private int totalRowCount;

	@Getter
	private Pageable pageable = PageRequest.of(pageNumber, this.getPageSize());

	private FooterRow footerRow;

	public PaginationGrid(Class<T> beanType, Class<F> filterType) {
		super(beanType);
		
		initGrid();

		try {
			filter = (F) filterType.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
		}
		
		
		binder = new Binder<F>(filterType);
	}


	public interface PaginationGridListener<T,F> {
		List<T> load(Pageable pageable, F filter);
	}

	@Override
	public void setPageSize(int pageSize) {
		pageable = PageRequest.of(pageNumber, pageSize);
		super.setPageSize(pageSize);
	}
	
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
		pageable = PageRequest.of(pageNumber, this.getPageSize());
		prev.setEnabled(pageNumber > 0);
	}

//	@Override
//	public void setItems(Collection<T> items) {
//		if (paginationGridListener != null) {
//			super.setItems(paginationGridListener.load(pageable, filter));
//		}
//	}

	public void setTotalRowCount(int totalRowCount) {
		this.totalRowCount = totalRowCount;
	}

	@Setter
	PaginationGridListener<T,F> paginationGridListener;
	private HeaderRow appendHeaderRow;

	public void initGrid() {

		this.setHeightByRows(true);
		
		
		pageLabel = new Label(String.valueOf(pageNumber + 1));

		prev = new Button(VaadinIcon.BACKWARDS.create());
		prev.setEnabled(pageNumber > 0);
		prev.addClickListener(o -> {
			prevPage();
			if (paginationGridListener != null) {
				this.setItems(paginationGridListener.load(pageable, filter));
				pageLabel.setText(String.valueOf(pageNumber + 1));
			}
		});

		next = new Button(VaadinIcon.FORWARD.create());
		next.addClickListener(o -> {
			if (paginationGridListener != null) {
				nextPage();
				List<T> newData = paginationGridListener.load(pageable, filter);
				if (newData == null) {
					prevPage();
					return;
				}
				pageLabel.setText(String.valueOf(pageNumber + 1));
				this.setItems(newData);
			}
		});

		HorizontalLayout btnLayout = new HorizontalLayout(prev, pageLabel, next);
		btnLayout.setWidthFull();
		btnLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		btnLayout.setJustifyContentMode(JustifyContentMode.CENTER);

		this.appendFooterRow();
		footerRow = this.appendFooterRow();
		footerRow.join(footerRow.getCells());
		FooterCell cell = footerRow.getCells().iterator().next();
		cell.setComponent(btnLayout);

	}
	
	private int nextPage() {
		setPageNumber(++pageNumber);
		return pageNumber;
	}

	private int prevPage() {
		if (pageNumber > 0) {
			setPageNumber(--pageNumber);
		}
		return pageNumber;
	}
	
	public void addFilterRow() {
		appendHeaderRow = this.appendHeaderRow();
		this.getColumns().forEach(c -> {
			if (c.getKey() != null) {
				String fieldName = c.getKey();
				try {

					TextField tf = new TextField();
					tf.setWidthFull();
					tf.setClearButtonVisible(true);
					tf.setValueChangeMode(ValueChangeMode.EAGER);

					Field declaredField = filter.getClass().getDeclaredField(fieldName);
					if (declaredField.getType() == BigDecimal.class) {
						binder.forField(tf).withConverter(new StringToBigDecimalConverter("")).bind(fieldName);
					} else if (declaredField.getType() == Boolean.class) {
						binder.forField(tf).withConverter(new StringToBooleanConverter("")).bind(fieldName);
					} else if (declaredField.getType() == Float.class) {
						binder.forField(tf).withConverter(new StringToFloatConverter("")).bind(fieldName);
					} else if (declaredField.getType() == Integer.class) {
						binder.forField(tf).withConverter(new StringToIntegerConverter("")).bind(fieldName);
					} else {
						binder.forField(tf).withNullRepresentation("").bind(fieldName);
					}

					tf.addValueChangeListener(v -> {
						try {
							binder.writeBean(filter);
							if (paginationGridListener != null) {
								setPageNumber(0);
								this.setItems(paginationGridListener.load(pageable, filter));
								pageLabel.setText(String.valueOf(pageNumber + 1));
							}
						} catch (ValidationException e) {
							e.printStackTrace();
						}
						//log.info("fieldName = "+fieldName+", filter = "+filter.toString());
//						this.setFilter(filter);
					});
					appendHeaderRow.getCell(c).setComponent(tf);
//					filterComponents.put(fieldName, tf);

				} catch (NoSuchFieldException | SecurityException e1) {
					//e1.printStackTrace();
					log.trace("Nincs ilyen oszlop! {}", fieldName);
				}
			}

		});
		
		
	}
	

}
