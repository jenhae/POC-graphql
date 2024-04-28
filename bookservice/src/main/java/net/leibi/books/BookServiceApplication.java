package net.leibi.books;

import graphql.execution.instrumentation.Instrumentation;
import graphql.execution.instrumentation.tracing.TracingInstrumentation;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import lombok.extern.log4j.Log4j2;
import net.leibi.books.generated.types.Book;
import net.leibi.books.service.DataService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@Log4j2
public class BookServiceApplication {
  private final DataService dataService;

  @Value("${application.upperbound:1000}")
  private int upperBound;

  public BookServiceApplication(DataService dataService) {
    this.dataService = dataService;
  }

  public static void main(String[] args) {
    SpringApplication.run(BookServiceApplication.class, args);
  }

  @PostConstruct
  public void init() {
    List<Book> data = getRandomData();
    dataService.add(data);
  }

  private List<Book> getRandomData() {
    log.info("Creating {} random books", upperBound);
    return IntStream.range(1, upperBound)
        .mapToObj(i -> new Book(UUID.randomUUID().toString(), String.valueOf(i), i))
        .toList();
  }

  @Bean
  @ConditionalOnProperty( prefix = "graphql.tracing", name = "enabled", matchIfMissing = true)
  public Instrumentation tracingInstrumentation(){
    return new TracingInstrumentation();
  }
}