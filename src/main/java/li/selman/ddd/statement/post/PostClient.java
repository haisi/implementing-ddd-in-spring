package li.selman.ddd.statement.post;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

import java.util.List;

public interface PostClient {
  @GetExchange("/posts")
  List<Post> findAll();

  @GetExchange("/posts/{id}")
  Post findById(@PathVariable Integer id);
}
