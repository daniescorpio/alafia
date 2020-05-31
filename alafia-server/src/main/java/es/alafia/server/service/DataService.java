package es.alafia.server.service;

import es.alafia.server.model.*;
import es.alafia.server.model.dto.AddDrinkDTO;
import es.alafia.server.model.dto.ClientDTO;
import es.alafia.server.model.dto.UpdateCourseDTO;
import es.alafia.server.model.exception.RequestedItemNotFoundException;
import es.alafia.server.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class DataService {

    private final RestaurantRepository restaurantRepository;
    private final DinnerTableRepository dinnerTableRepository;
    private final BookingRepository bookingRepository;
    private final ClientRepository clientRepository;
    private final OrderRepository orderRepository;
    private final CourseRepository courseRepository;
    private final DrinkRepository drinkRepository;

    public boolean checkDBEmpty() {
        return restaurantRepository.findAll().isEmpty();
    }

    public List<Restaurant> retrieveRestaurantsData() {
        log.info("Fetching restaurants...");
        return restaurantRepository.findAll();
    }

    public List<DinnerTable> retrieveDinnerTablesData() {
        log.info("Fetching dinner tables...");
        return dinnerTableRepository.findAll();
    }

    public List<Booking> retrieveBookingsData() {
        log.info("Fetching bookings...");
        return bookingRepository.findAll();
    }

    public List<Client> retrieveClientsData() {
        log.info("Fetching clients...");
        return clientRepository.findAll();
    }

    public List<Order> retrieveOrdersData() {
        log.info("Fetching orders...");
        return orderRepository.findAll();
    }

    public List<Course> retrieveCoursesData() {
        log.info("Fetching courses");
        return courseRepository.findAll();
    }

    public List<Drink> retrieveDrinksData() {
        log.info("Fetching drinks...");
        return drinkRepository.findAll();
    }

    public Restaurant saveNewRestaurant(Restaurant restaurant) {
        log.info("Saving new restaurant...");
        return restaurantRepository.save(restaurant);
    }

    public DinnerTable saveNewDinnerTable(DinnerTable dinnerTable) {
        return dinnerTableRepository.save(dinnerTable);
    }

    public Booking saveNewBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    public Client saveNewClient(ClientDTO client) throws RequestedItemNotFoundException {
        log.info("Saving new client for booking: {}", client.getBookingId());
        Client newClient = Client.builder()
                .name(client.getName())
                .mail(client.getMail())
                .build();
        Client savedClient = clientRepository.save(newClient);
        updateParentsWithNewClientData(client, savedClient);
        return savedClient;
    }

    public Order saveNewOrder(Order order) {
        return orderRepository.save(order);
    }

    public Course saveNewCourse(Course course) {
        return courseRepository.save(course);
    }

    public Drink saveNewDrink(Drink drink) {
        return drinkRepository.save(drink);
    }

    //TODO: Move under basic rest operations

    public DinnerTable retrieveTable(String tableId) throws RequestedItemNotFoundException {
        try {
            return dinnerTableRepository.findById(tableId).orElseThrow();
        } catch (Exception e) {
            throw new RequestedItemNotFoundException("Table with id " + tableId + " not found in DB");
        }
    }

    private void updateParentsWithNewClientData(ClientDTO client, Client newClient) throws RequestedItemNotFoundException {
        Booking booking;
        try {
            booking = bookingRepository.findById(client.getBookingId()).orElseThrow();
        } catch (Exception e) {
            throw new RequestedItemNotFoundException("Booking with id " + client.getBookingId() + " not found in DB");
        }
        booking.getDiners().add(
                newClient);
        bookingRepository.save(booking);

        DinnerTable dinnerTable;
        try {
            dinnerTable = dinnerTableRepository.findById(client.getDinnerTableId()).orElseThrow();
        }
        catch (Exception e) {
            throw new RequestedItemNotFoundException("Booking with id " + client.getBookingId() + " not found in DB");
        }
        dinnerTable.setBooking(booking);
        dinnerTableRepository.save(dinnerTable);

        Restaurant restaurant;
        try {
            restaurant = restaurantRepository.findById(client.getRestaurantId()).orElseThrow();
        }
        catch (Exception e) {
            throw new RequestedItemNotFoundException("Booking with id " + client.getBookingId() + " not found in DB");
        }
        restaurant.getDinnerTables().stream().filter(table -> table.getId().equals(client.getDinnerTableId())).findFirst().orElseThrow().setBooking(booking);
        restaurantRepository.save(restaurant);
    }

    public Client addDrinkInClient(AddDrinkDTO addDrinkDTO) throws RequestedItemNotFoundException {
        Client client;
        Drink drink;
        try {
            client = clientRepository.findById(addDrinkDTO.getClientId()).orElseThrow();
            log.info("Client with id {} retrieved from DB correctly", addDrinkDTO.getClientId());
        } catch (Exception e) {
            throw new RequestedItemNotFoundException("Client with id " + addDrinkDTO.getClientId() + " not found in DB");
        }
        try {
            drink = drinkRepository.findById(addDrinkDTO.getDrinkId()).orElseThrow();
            log.info("Drink with id {} retrieved from DB correctly", addDrinkDTO.getDrinkId());
        } catch (Exception e) {
            throw new RequestedItemNotFoundException("Drink with id " + addDrinkDTO.getDrinkId() + " not found in DB");
        }
        client.getOrder().getDrinks().add(drink);
        Client clientWithDrinkAgreed = clientRepository.save(client);
        log.info("Drink agreed correctly in client");
        return clientWithDrinkAgreed;
    }

    public Course updateCourseStatus(UpdateCourseDTO courseDTO) throws RequestedItemNotFoundException {
        Client client;
        Course course;
        try {
            client = clientRepository.findById(courseDTO.getClientId()).orElseThrow();
            log.info("Client with id {} retrieved from DB correctly", courseDTO.getClientId());
        }
        catch (Exception e) {
            throw new RequestedItemNotFoundException("Client with id " + courseDTO.getClientId() + " not found in DB");
        }
        try {
            course = courseRepository.findById(courseDTO.getCourseId()).orElseThrow();
            log.info("Course with id {} retrieved from DB correctly", courseDTO.getCourseId());
        }
        catch (Exception e) {
            throw new RequestedItemNotFoundException("Client with id " + courseDTO.getClientId() + " not found in DB");
        }
        boolean newCourseStatus = !course.isServed();
        course.setServed(newCourseStatus);
        courseRepository.save(course);
        log.info("New status for course with id {} is {}", courseDTO.getCourseId(), newCourseStatus);
        client.getOrder().getCourses().stream()
                .filter(c -> c.getId().equals(courseDTO.getCourseId()))
                .findFirst()
                .orElseThrow()
                .setServed(newCourseStatus);
        clientRepository.save(client);
        log.info("Client with id {} updated with new course status", courseDTO.getClientId());
        return course;
    }
}
