package org.chaby.GridPaginatiopnDemo;

import java.util.List;

import javax.annotation.PostConstruct;

import org.chaby.GridPaginatiopnDemo.PaginationGrid.PaginationGridListener;
import org.chaby.GridPaginatiopnDemo.entity.OtpKivonat;
import org.chaby.GridPaginatiopnDemo.repository.OtpKivonatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Route
@Component
@PreserveOnRefresh
public class MainView extends VerticalLayout implements PaginationGridListener<OtpKivonat,OtpKivonat> {
	
	@Autowired
	OtpKivonatRepository kivonatRepository;
	
	private PaginationGrid<OtpKivonat,OtpKivonat> grid;

	@PostConstruct
	public void init() {
		grid = new PaginationGrid(OtpKivonat.class, OtpKivonat.class);
		grid.setPageSize(15);
		grid.setPaginationGridListener(this);
		grid.setItems(load(grid.getPageable(), grid.getFilter()));
		grid.addFilterRow();
		
		add(grid);
		
		
	}
	
	@Override
	public List<OtpKivonat> load(Pageable pageable, OtpKivonat filter) {
		
		List<OtpKivonat> data;
		if (filter != null) {
			ExampleMatcher matcher = ExampleMatcher
					.matching()
					.withIgnoreCase()
					.withIgnoreNullValues()
					.withMatcher("enev", match -> match.contains())
					;
			
			data = kivonatRepository.findAll(Example.of(filter,matcher), pageable).getContent();
			if (data.isEmpty() && pageable.getPageNumber() > 0) {
				return null;
			}
			return data;
		} else {
			data = kivonatRepository.findAll(pageable).getContent();
			if (data.isEmpty() && pageable.getPageNumber() > 0) {
				return null;
			}
			return data;
		}
	}


}
