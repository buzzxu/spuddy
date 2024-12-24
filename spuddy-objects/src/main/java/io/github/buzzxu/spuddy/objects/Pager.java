package io.github.buzzxu.spuddy.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

/**
 * Created by xux on 2017/3/31.
 */
public class Pager<T> implements Serializable {

    private static final int DEFAULT_PAGE_SIZE = 5;
    @Serial
    private static final long serialVersionUID = 4961676958200729539L;

    @Getter @Setter
    private List<T> data;
    @Getter @Setter
    private int pageNumber;
    @Getter @Setter
    private int pageSize;
    @Getter @Setter
    private int totalPage;
    @Getter @Setter
    private long totalRow;
    private boolean count;
    private boolean hasNext = false;

    public static final Pager DEFAULT = new Pager();

    public Pager(){
        this(1);
        this.data = Collections.emptyList();
    }
    public Pager(int pageNumber) {
        this(true,pageNumber,DEFAULT_PAGE_SIZE);
    }
    public Pager(int pageNumber,int pageSize) {
        this(true,pageNumber,pageSize);
    }

    public Pager(boolean count, int pageNumber, int pageSize) {
        this.count = count;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    @JsonIgnore
    public int firstRow(){
        return  (pageNumber -1) * pageSize;
    }

    public boolean isCount() {
        return count;
    }

    public void notCount(){
        this.count = false;
    }

    public boolean isFirstPage() {
        return pageNumber == 1;
    }

    public boolean isLastPage() {
        return pageNumber >= totalPage;
    }

    public void hasNext(boolean hasNext){
        this.hasNext = hasNext;
    }

    public boolean isHasNext() {
        return pageNumber < totalPage;
    }

    @JsonIgnore
    public void computeTotalPage(){
        if ( totalRow > 0) {
            totalPage = Math.toIntExact(totalRow / pageSize + ((totalRow % pageSize == 0) ? 0 : 1));
        }
    }


    public <E>  Pager<T> transform(Pager<E> page, Function<List<E>,List<T>> function){
        this.data = function.apply(page.data);
        this.pageNumber = page.getPageNumber();
        this.pageSize = page.getPageSize();
        this.totalPage = page.getTotalPage();
        this.totalRow = page.getTotalRow();
        return this;
    }
    public <E>  Pager<E> transform(Function<List<T>,List<E>> function){
        Pager<E> pager = new Pager<>();
        pager.data = function.apply(data);
        pager.pageNumber = this.getPageNumber();
        pager.pageSize = this.getPageSize();
        pager.totalPage = this.getTotalPage();
        pager.totalRow = this.getTotalRow();
        return pager;
    }
    public <R>  Pager<R> convert(Function<? super T, ? extends R> mapper){
        List<R> collect = data.stream().map(mapper).collect(toList());
        return ((Pager<R>) this).setDatas(collect);
    }
    public Pager<T> peek(Consumer<? super T> consumer){
        data = data.stream().peek(consumer).toList();
        return this;
    }

    public Pager<T> setDatas(List<T> data){
        this.data = data;
        return this;
    }


    public static <T> Pager<T> pagine(List<T> list, Integer pageNumber, Integer pageSize) {
        Pager<T> pager = new Pager<>(pageNumber,pageSize);
        if(list == null || list.isEmpty()){
            return pager;
        }
        Integer count = list.size(); //记录总数
        int pageCount; //页数
        if (count % pageSize == 0) {
            pageCount = count / pageSize;
        } else {
            pageCount = count / pageSize + 1;
        }
        pager.setTotalPage(pageCount);
        pager.setTotalRow(count);

        int fromIndex = 0; //开始索引
        int toIndex = 0; //结束索引

        if(pageNumber > pageCount){
            pageNumber = pageCount;
        }
        fromIndex = (pageNumber - 1) * pageSize;
        if (!pageNumber.equals(pageCount)) {
            toIndex = fromIndex + pageSize;
        } else {
            toIndex = count;
        }
        pager.setData(list.subList(fromIndex, toIndex));
        return pager;
    }
    public static void main(String[] args) {
        List<Integer> ids = List.of(1,2,3,4,5,6,7,8,9,10,11);
        System.out.println(Pager.pagine(ids,15,3));
    }
    @Override
    public String toString() {
        return new StringJoiner(", ", Pager.class.getSimpleName() + "[", "]")
                .add("data=" + data)
                .add("pageNumber=" + pageNumber)
                .add("pageSize=" + pageSize)
                .add("totalPage=" + totalPage)
                .add("totalRow=" + totalRow)
                .add("count=" + count)
                .add("hasNext=" + hasNext)
                .toString();
    }
}
