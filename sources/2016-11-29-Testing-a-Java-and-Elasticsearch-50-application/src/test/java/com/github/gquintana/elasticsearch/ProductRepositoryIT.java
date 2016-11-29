package com.github.gquintana.elasticsearch;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class ProductRepositoryIT {
    private final ProductRepository repository = new ProductRepository();

    @Before
    public void setUp() throws Exception {
        repository.initialize();
    }


    @After
    public void tearDown() throws Exception {
        repository.deleteAll();
        repository.close();
    }

    @Test
    public void testIndexAndGet() throws Exception {
        // Given
        Product product = new Product("75149", "Resistance X-Wing Fighter", "Help! Lor San Tekka's desert home is under attack by the First Order Flametrooper! Swoop to the rescue with Poe Dameron in the Resistance X-Wing Fighter with its opening wings and cockpit, 4 spring-loaded shooters, retractable landing gear, removable hyperdrive at the back and detachable BB-8 Astromech Droid. You must help Lor San Tekka before the trooper destroys his home!");
        // When
        repository.index(product);
        // Then
        Thread.sleep(1000L);
        Product product1 = repository.get("75149");
        assertThat(product1.getId(), equalTo(product.getId()));
        assertThat(product1.getTitle(), equalTo(product.getTitle()));
        assertThat(product1.getDescription(), equalTo(product.getDescription()));
    }

    @Test
    public void testCreateAndGet() throws Exception {
        // Given
        Product product = new Product(null, "New", "Something really new");
        // When
        repository.index(product);
        // Then
        assertThat(product.getId(), notNullValue());
        Thread.sleep(1000L);
        Product product1 = repository.get(product.getId());
        assertThat(product1.getTitle(), equalTo(product.getTitle()));
        assertThat(product1.getDescription(), equalTo(product.getDescription()));
    }

    @Test
    public void testGetNotFound() throws Exception {
        // When
        Product product = repository.get("123456");
        // Then
        assertThat(product, is(nullValue()));
    }

    @Test
    public void testDelete() throws Exception {
        // Given
        Product product = new Product("1", "To delete", "Something to delete");
        repository.index(product);
        // When
        repository.delete("1");
        // Then
        Thread.sleep(1000L);
        Product product1 = repository.get("1");
        assertThat(product1, is(nullValue()));
    }

}