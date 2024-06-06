package ru.frolov.catalogservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.frolov.catalogservice.controller.payload.NewProductPayload;
import ru.frolov.catalogservice.entity.Product;
import ru.frolov.catalogservice.service.ProductService;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("catalogue-api/products")
public class ProductsRestController {

    private final ProductService productService;
    private final MessageSource messageSource;

    @GetMapping
    public List<Product> findProducts() {
        return this.productService.findAllProducts();
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody NewProductPayload payload,
                                           BindingResult bindingResult,
                                           UriComponentsBuilder uriComponentsBuilder,
                                           Locale locale) {
        if (bindingResult.hasErrors()) {
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, this.messageSource.getMessage("errors.400.title",
                    new Object[0], "errors.400.title", locale));
            problemDetail.setProperty("errors", bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList());
            return ResponseEntity.badRequest().body(problemDetail);
        } else {
            Product product = this.productService.createProduct(payload.title(), payload.details());
            return ResponseEntity.created(uriComponentsBuilder
                            .replacePath("/catalogue-api/products/{productId}")
                            .build(Map.of("productId", product.getId())))
                    .body(product);
            // 1. разбор метода ResponseEntity.created(URI): - создает и возвращает BodyBuilder со статусом CREATED и с заголовком location = URI
            //      public static ResponseEntity.BodyBuilder created(URI location)
            //      Create a new builder with a CREATED status and a location header set to the given URI.

            // 2. разбор метода body(T body): - принадлежит объекту BodyBuilder. Возвращает ResponseEntity с установленным телом T
            //      <T> ResponseEntity<T> body(@Nullable T body)
            //Set the body of the response entity and returns it.
            //Type Parameters:
            //T - the type of the body
            //Parameters:
            //body - the body of the response entity
            //Returns:
            //the built response entity


            //URI location = uriComponentsBuilder.replacePath("/catalogue-api/products/{productId}").build(Map.of("productId", product.getId()));
            //      1.1 разбор метода replacePath(string): - меняет path и возвращает другой UriComponentBuilder с измененной path
            //      -----------------------------------
            //      public UriComponentsBuilder replacePath(@NullableString path)
            //      Parameters:
            //      path - the URI path, or null for an empty path

            //      1.2 разбор метода build(map): - создает и возвращает URI с взятыми из map переменными
            //      ---------------------------
            //      public URI build(Map<String,?> uriVariables)
            //      Build a URI instance and replaces URI template variables with the values from a map.
            //      Parameters:
            //      uriVariables - the map of URI variables


            // ResponseEntity помимо headers и body (унаследованных от HttpEntity) имеет еще HttpStatusCode.
            // У него встроенные интерфейсы:
            //      BodyBuilder (добавляет тело в ResponseEntity),
            //      HeadersBuilder (добавляет заголовки в ResponseEntity)
            // метод created(uri) возвращает:
            //      BodyBuilder со статусом CREATED и заголовком location, соответствующим данному uri.
            // ***BodyBuilder имеет метод:
            //      body(T body) - устанавливает в ResponseEntity тело T и возвращает эту ResponseEntity уже с телом;

        }
    }
}
