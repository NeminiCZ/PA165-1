package cz.fi.muni.pa165.tasks;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.validation.ConstraintViolationException;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import cz.fi.muni.pa165.PersistenceSampleApplicationContext;
import cz.fi.muni.pa165.entity.Category;
import cz.fi.muni.pa165.entity.Product;

 
@ContextConfiguration(classes = PersistenceSampleApplicationContext.class)
public class Task02 extends AbstractTestNGSpringContextTests {

	private Category kitchen, electro;
	private Product flashlight, kitchenRobot, plate;


	@BeforeClass
	private void beforeClassTest() {
		Category kitchen = new Category();
		kitchen.setName("Kitchen");
		Category electro = new Category();
		electro.setName("Electro");
		Product flashlight = new Product();
		flashlight.setName("Flashlight");
		flashlight.addCategory(electro);
		Product kitchenRobot = new Product();
		kitchenRobot.setName("Kitchen Robot");
		kitchenRobot.addCategory(electro);
		kitchenRobot.addCategory(kitchen);
		Product plate = new Product();
		plate.setName("Plate");
		plate.addCategory(kitchen);

		kitchen.addProduct(plate);
		kitchen.addProduct(kitchenRobot);
		electro.addProduct(kitchenRobot);
		electro.addProduct(flashlight);

		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.persist(kitchen);
		em.persist(electro);
		em.persist(flashlight);
		em.persist(kitchenRobot);
		em.persist(plate);
		em.getTransaction().commit();
		em.close();

		this.electro = electro;
		this.kitchen = kitchen;
		this.flashlight = flashlight;
		this.kitchenRobot = kitchenRobot;
		this.plate = plate;
	}

	@PersistenceUnit
	private EntityManagerFactory emf;

	@Test
	public void searchEntityElectroTest() {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();

		Category category = em.find(Category.class, electro.getId());

		assertContainsProductWithName(category.getProducts(), "Kitchen Robot");
		assertContainsProductWithName(category.getProducts(), "Flashlight");

		em.getTransaction().commit();
		em.close();
	}

	@Test
	public void searchEntityKitchenTest() {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();

		Category category = em.find(Category.class, kitchen.getId());

		assertContainsProductWithName(category.getProducts(), "Kitchen Robot");
		assertContainsProductWithName(category.getProducts(), "Plate");

		em.getTransaction().commit();
		em.close();
	}


	@Test
	public void searchEntityPlateTest() {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();

		Product product = em.find(Product.class, plate.getId());

		assertContainsCategoryWithName(product.getCategories(), "Kitchen");

		em.getTransaction().commit();
		em.close();
	}


	@Test
	public void searchEntityRobotTest() {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();

		Product product = em.find(Product.class, kitchenRobot.getId());

		assertContainsCategoryWithName(product.getCategories(), "Kitchen");
		assertContainsCategoryWithName(product.getCategories(), "Electro");

		em.getTransaction().commit();
		em.close();
	}


	@Test
	public void searchEntityFlashlightTest() {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();

		Product product = em.find(Product.class, flashlight.getId());

		assertContainsCategoryWithName(product.getCategories(), "Electro");

		em.getTransaction().commit();
		em.close();
	}

	private void assertContainsCategoryWithName(Set<Category> categories,
			String expectedCategoryName) {
		for(Category cat: categories){
			if (cat.getName().equals(expectedCategoryName))
				return;
		}
			
		Assert.fail("Couldn't find category "+ expectedCategoryName+ " in collection "+categories);
	}
	private void assertContainsProductWithName(Set<Product> products,
			String expectedProductName) {
		
		for(Product prod: products){
			if (prod.getName().equals(expectedProductName))
				return;
		}
			
		Assert.fail("Couldn't find product "+ expectedProductName+ " in collection "+products);
	}

	
}
