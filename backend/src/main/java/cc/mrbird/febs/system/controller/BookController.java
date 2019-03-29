package cc.mrbird.febs.system.controller;

import cc.mrbird.febs.common.annotation.Log;
import cc.mrbird.febs.common.controller.BaseController;
import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.system.domain.Book;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequestMapping("/book")
public class BookController extends BaseController {

    private String message;
	
    private static List<Book> bookList;
    
    private static AtomicInteger id = new AtomicInteger(4);
    
    static {
    	bookList = new ArrayList<Book>() {
			private static final long serialVersionUID = -5329779579501684090L;
		
			{
				add(new Book(1, "java 8 in action", "jack", new Date(100, 1, 20)));
				add(new Book(2, "spring boot action", "jack ma", new Date(104, 1, 20)));
				add(new Book(3, "RocketMQ 实战与源码分析", "阿里巴巴", new Date(109, 6, 23)));
			}
    	};
  
	}

    @GetMapping
    @RequiresPermissions("book:view")
    public Map<String, Object> BookList(QueryRequest request, Book book) {
		Map<String, Object> rspData = new HashMap<>();
		
		if (book == null || (StringUtils.isEmpty(book.getAuthor()) && StringUtils.isEmpty(book.getName()))) {
			rspData.put("rows", bookList);
			rspData.put("total", bookList.size());
			return rspData;
		}
		
		List<Book> result = bookList.stream().filter(book1 ->
				(book.getAuthor() != null && book1.getAuthor().equalsIgnoreCase(book.getAuthor())) ||
				(book.getName() != null && book1.getName().equalsIgnoreCase(book.getName()))
		).collect(Collectors.toList());
		rspData.put("rows", result);
		rspData.put("total", result.size());
		return rspData;
		
    }

    @Log("新增书籍")
    @PostMapping
    @RequiresPermissions("book:add")
    public void addBook(@Valid Book book) throws FebsException {
        try {
        	book.setId(id.getAndIncrement());
        	bookList.add(book);
        } catch (Exception e) {
            message = "新增书籍成功";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    @Log("删除书籍")
    @DeleteMapping("/{ids}")
    @RequiresPermissions("book:delete")
    public void deleteBooks(@NotBlank(message = "{required}") @PathVariable String ids) throws FebsException {
        try {
            String[] ids2 = ids.split(StringPool.COMMA);
            
            for (String id : ids2) {
				bookList.removeIf(book -> book.getId().toString().equals(id));
			}
			
        } catch (Exception e) {
            message = "删除书籍成功";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    @Log("修改书籍")
    @PutMapping
    @RequiresPermissions("book:update")
    public void updateBook(@Valid Book book) throws FebsException {
        try {
        	for (Book book1 : bookList) {
        		if (book1.getId().equals(book.getId())) {
        			book1.setName(book.getName());
        			book1.setAuthor(book.getAuthor());
        			book1.setPulishDate(book.getPulishDate());
					break;
				}
			}
        
        } catch (Exception e) {
            message = "修改书籍成功";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    @PostMapping("excel")
    @RequiresPermissions("book:export")
    public void export(QueryRequest request, Book book, HttpServletResponse response) throws FebsException {
        try {
            // List<Book> books = this.bookService.findBooks(request, book).getRecords();
            // ExcelKit.$Export(Book.class, response).downXlsx(books, false);
			System.out.println("导出 excel 成功");
		} catch (Exception e) {
            message = "导出Excel失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }
}
