package com.iosre.pphb.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public class Page<T> implements Pageable {
	
	private long totalRecord;
    private int pageNo = 1;
    private int pageSize = 10;
    private List<T> results;

    @JsonIgnore
    private Sort sort;
    
    public Page(int pageNo, int pageSize){
    	this.pageNo = pageNo;
    	this.pageSize = pageSize;
    }

    public Page(int pageNo, int pageSize, List<T> results){
    	this(pageNo, pageSize);
    	this.results = results;
    }

    
	public int getTotalPage() {
		return (int) (totalRecord+pageSize-1)/pageSize;
	}
	
    public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public long getTotalRecord() {
		return totalRecord;
	}

	public void setTotalRecord(long totalRecord) {
		this.totalRecord = totalRecord;
	}

	public void setSort(Sort sort) {
		this.sort = sort;
	}

	public List<T> getResults() {
		return results;
	}

	public void setResults(List<T> results) {
		this.results = results;
	}

	@Override
	public int getPageNumber() {
		return pageNo;
	}

	@Override
	@JsonIgnore
	public int getOffset() {
		return (pageNo-1)*pageSize;
	}

	@Override
	public Sort getSort() {
		return sort;
	}

	@Override
	public Pageable next() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pageable previousOrFirst() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pageable first() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasPrevious() {
		// TODO Auto-generated method stub
		return false;
	}
}
