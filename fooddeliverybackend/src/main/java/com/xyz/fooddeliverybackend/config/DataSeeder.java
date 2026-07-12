package com.xyz.fooddeliverybackend.config;

import com.xyz.fooddeliverybackend.model.Restaurant;
import com.xyz.fooddeliverybackend.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import com.xyz.fooddeliverybackend.model.MenuItem;
import com.xyz.fooddeliverybackend.repository.MenuItemRepository;



@Component
@Profile("!test")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;

    @Override
    public void run(String... args) {

        if (restaurantRepository.count() == 0) {

            Restaurant r1 = new Restaurant();
            r1.setName("Burger King");
            r1.setImage("burgerking.jpg");
            r1.setRating(4.5);
            r1.setDeliveryTime("25 mins");
            r1.setCategory("Fast Food");

            Restaurant r2 = new Restaurant();
            r2.setName("Pizza Hut");
            r2.setImage("pizzahut.jpg");
            r2.setRating(4.4);
            r2.setDeliveryTime("30 mins");
            r2.setCategory("Pizza");

            Restaurant r3 = new Restaurant();
            r3.setName("Domino's");
            r3.setImage("dominos.jpg");
            r3.setRating(4.3);
            r3.setDeliveryTime("22 mins");
            r3.setCategory("Pizza");

            Restaurant r4 = new Restaurant();
            r4.setName("Subway");
            r4.setImage("subway.jpg");
            r4.setRating(4.6);
            r4.setDeliveryTime("20 mins");
            r4.setCategory("Healthy");

            Restaurant r5 = new Restaurant();
            r5.setName("Biryani House");
            r5.setImage("biryani.jpg");
            r5.setRating(4.7);
            r5.setDeliveryTime("35 mins");
            r5.setCategory("Indian");

            restaurantRepository.save(r1);
            restaurantRepository.save(r2);
            restaurantRepository.save(r3);
            restaurantRepository.save(r4);
            restaurantRepository.save(r5);

            System.out.println("Seed data inserted!");
        }

        if (menuItemRepository.count() == 0) {

            // Burger King
            restaurantRepository.findByName("Burger King").ifPresent(restaurant -> {

                String id = restaurant.getId();

                saveMenuItem(id, "Chicken Burger", "Juicy chicken burger", 199.0, "chickenburger.jpg");
                saveMenuItem(id, "Classic Burger", "Classic grilled burger", 149.0, "burger.jpg");
                saveMenuItem(id, "Chicken Balls", "Crispy chicken balls", 129.0, "chickenball.jpg");
                saveMenuItem(id, "Chicken Fry", "Crispy fried chicken", 179.0, "chickenfry.jpg");
            });

            // Pizza Hut
            restaurantRepository.findByName("Pizza Hut").ifPresent(restaurant -> {

                String id = restaurant.getId();

                saveMenuItem(id, "Classic Pizza", "Cheesy pizza", 299.0, "pizza.jpg");
                saveMenuItem(id, "Italian Pizza", "Italian style pizza", 349.0, "italisnpizza.jpg");
                saveMenuItem(id, "Chocolate Cake", "Rich chocolate cake", 149.0, "cake.jpg");
                saveMenuItem(id, "Donut Dessert", "Sweet donut", 99.0, "donut.jpg");
            });

            // Domino's
            restaurantRepository.findByName("Domino's").ifPresent(restaurant -> {

                String id = restaurant.getId();

                saveMenuItem(id, "Loaded Pizza", "Pizza loaded with toppings", 329.0, "pizza.jpg");
                saveMenuItem(id, "Spicy Momo", "Steamed spicy momos", 149.0, "momo.jpg");
                saveMenuItem(id, "Veg Noodles", "Veg noodles", 129.0, "noodles.jpg");
                saveMenuItem(id, "Pancake", "Soft pancake", 119.0, "pancake.jpg");
            });

            // Subway
            restaurantRepository.findByName("Subway").ifPresent(restaurant -> {

                String id = restaurant.getId();

                saveMenuItem(id, "Garden Salad", "Fresh vegetable salad", 149.0, "salad.jpg");
                saveMenuItem(id, "Fresh Sandwich", "Loaded sandwich", 199.0, "sandwich.jpg");
                saveMenuItem(id, "Cupcake", "Soft cupcake", 99.0, "cupcake.jpg");
                saveMenuItem(id, "Ice Cream", "Vanilla ice cream", 89.0, "icecream.jpg");
            });

            // Biryani House
            restaurantRepository.findByName("Biryani House").ifPresent(restaurant -> {

                String id = restaurant.getId();

                saveMenuItem(id, "Special Sahidal", "Traditional special dish", 249.0, "sahidal.jpg");
                saveMenuItem(id, "Sea Fish Curry", "Spicy sea fish curry", 299.0, "seafishcurry.jpg");
                saveMenuItem(id, "Fish Fry", "Crispy fish fry", 199.0, "fishfry.jpg");
                saveMenuItem(id, "Sikh Kebab", "Tender kebab", 229.0, "sikhkebab.jpg");
            });

            System.out.println("20 menu items inserted!");
        }
    }

    private void saveMenuItem(
            String restaurantId,
            String name,
            String description,
            Double price,
            String image
    ) {

        MenuItem item = new MenuItem();

        item.setRestaurantId(restaurantId);
        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);
        item.setImage(image);

        menuItemRepository.save(item);
    }
}
