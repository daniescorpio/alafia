package es.alafia.server.controller;

import es.alafia.server.LoadInitData;
import es.alafia.server.model.*;
import es.alafia.server.model.exception.TableNotFoundException;
import es.alafia.server.service.DataService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = "/api", produces = "application/json;charset=UTF-8")
public class AlafiaController {

    private final DataService dataService;
    private final LoadInitData loadInitData;

    @GetMapping(value = "/load-data")
    public void loadMockedData() {
        if (dataService.checkDBEmpty()) {
            log.info("DB empty, loading initial data");
            loadInitData.loadData();
            log.info("Mocked data loaded in DB");
        } else {
            log.info("DB with data, load isn't necessary");
        }
    }

    @GetMapping(value = "/restaurants")
    public List<Restaurant> getRestaurantsData() {
        List<Restaurant> restaurants = dataService.retrieveRestaurantsData();
        log.info("Found {} restaurants in DB", restaurants.size());
        return restaurants;
    }

    @GetMapping(value = "/dinner-tables")
    public List<DinnerTable> getDinnerTablesData() {
        List<DinnerTable> dinnerTables = dataService.retrieveDinnerTablesData();
        log.info("Found {} dinner tables in DB", dinnerTables.size());
        return dinnerTables;
    }

    @GetMapping(value = "/bookings")
    public List<Booking> getBookingsData() {
        List<Booking> bookings = dataService.retrieveBookingsData();
        log.info("Found {} bookings in DB", bookings.size());
        return bookings;
    }

    @GetMapping(value = "/clients")
    public List<Client> getClientsData() {
        List<Client> clients = dataService.retrieveClientsData();
        log.info("Found {} clients in DB", clients.size());
        return clients;
    }

    @GetMapping(value = "/orders")
    public List<Order> getOrdersData() {
        List<Order> orders = dataService.retrieveOrdersData();
        log.info("Found {} orders in DB", orders.size());
        return orders;
    }

    @GetMapping(value = "/courses")
    public List<Course> getCoursesData() {
        List<Course> courses = dataService.retrieveCoursesData();
        log.info("Found {} courses in DB", courses.size());
        return courses;
    }

    @GetMapping(value = "/drinks")
    public List<Drink> getDrinksData() {
        List<Drink> drinks = dataService.retrieveDrinksData();
        log.info("Found {} drinks in DB", drinks.size());
        return drinks;
    }

    @GetMapping(value = "/active-table")
    public DinnerTable getActiveTable(String activeTableId) throws TableNotFoundException {
        return dataService.retrieveTable(activeTableId);
    }

    @PostMapping(value = "/restaurants")
    @ResponseStatus(HttpStatus.CREATED)
    public Restaurant saveNewRestaurant(@RequestBody Restaurant restaurant) {
        Restaurant newRestaurant = dataService.saveNewRestaurant(restaurant);
        log.info("Restaurant saved with id: {}", newRestaurant.getId());
        return newRestaurant;
    }

    @PostMapping(value = "/dinner-tables")
    @ResponseStatus(HttpStatus.CREATED)
    public DinnerTable saveNewDinnerTable(DinnerTable dinnerTable) {
        return dataService.saveNewDinnerTable(dinnerTable);
    }

    @PostMapping(value = "/bookings")
    @ResponseStatus(HttpStatus.CREATED)
    public Booking saveNewBooking(Booking booking) {
        return dataService.saveNewBooking(booking);
    }

    @PostMapping(value = "/clients")
    @ResponseStatus(HttpStatus.CREATED)
    public Client saveNewClient(@RequestBody ClientDTO client) {
        Client newClient = dataService.saveNewClient(client);
        log.info("Client saved with id: {}", newClient.getId());
        return newClient;
    }

    @PostMapping(value = "/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public Order saveNewOrder(Order order) {
        return dataService.saveNewOrder(order);
    }

    @PostMapping(value = "/courses")
    @ResponseStatus(HttpStatus.CREATED)
    public Course saveNewCourse(Course course) {
        return dataService.saveNewCourse(course);
    }

    @PostMapping(value = "/drinks")
    @ResponseStatus(HttpStatus.CREATED)
    public Drink saveNewDrink(Drink drink) {
        return dataService.saveNewDrink(drink);
    }
}


