package com.example.Edi.myapplication.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "cityApi",
        version = "v1",
        resource = "city",
        namespace = @ApiNamespace(
                ownerDomain = "backend.myapplication.Edi.example.com",
                ownerName = "backend.myapplication.Edi.example.com",
                packagePath = ""
        )
)
public class CityEndpoint {

    private static final Logger logger = Logger.getLogger(CityEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(City.class);
    }

    /**
     * Returns the {@link City} with the corresponding ID.
     *
     * @param Id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code City} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "city/{Id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public City get(@Named("Id") Long Id) throws NotFoundException {
        logger.info("Getting City with ID: " + Id);
        City city = ofy().load().type(City.class).id(Id).now();
        if (city == null) {
            throw new NotFoundException("Could not find City with ID: " + Id);
        }
        return city;
    }

    /**
     * Inserts a new {@code City}.
     */
    @ApiMethod(
            name = "insert",
            path = "city",
            httpMethod = ApiMethod.HttpMethod.POST)
    public City insert(City city) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that city.Id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(city).now();
        logger.info("Created City with ID: " + city.getId());

        return ofy().load().entity(city).now();
    }

    /**
     * Updates an existing {@code City}.
     *
     * @param Id   the ID of the entity to be updated
     * @param city the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code Id} does not correspond to an existing
     *                           {@code City}
     */
    @ApiMethod(
            name = "update",
            path = "city/{Id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public City update(@Named("Id") Long Id, City city) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(Id);
        ofy().save().entity(city).now();
        logger.info("Updated City: " + city);
        return ofy().load().entity(city).now();
    }

    /**
     * Deletes the specified {@code City}.
     *
     * @param Id the ID of the entity to delete
     * @throws NotFoundException if the {@code Id} does not correspond to an existing
     *                           {@code City}
     */
    @ApiMethod(
            name = "remove",
            path = "city/{Id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("Id") Long Id) throws NotFoundException {
        checkExists(Id);
        ofy().delete().type(City.class).id(Id).now();
        logger.info("Deleted City with ID: " + Id);
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "list",
            path = "city",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<City> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<City> query = ofy().load().type(City.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<City> queryIterator = query.iterator();
        List<City> cityList = new ArrayList<City>(limit);
        while (queryIterator.hasNext()) {
            cityList.add(queryIterator.next());
        }
        return CollectionResponse.<City>builder().setItems(cityList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long Id) throws NotFoundException {
        try {
            ofy().load().type(City.class).id(Id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find City with ID: " + Id);
        }
    }
}