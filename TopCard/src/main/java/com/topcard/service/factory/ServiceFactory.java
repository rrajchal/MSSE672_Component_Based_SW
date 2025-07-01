package com.topcard.service.factory;

import com.topcard.domain.Player;
import com.topcard.exceptions.TopCardException;
import com.topcard.marker.TopCardMarker;
import com.topcard.service.game.GameService;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;


/**
 * ServiceFactory is a factory class responsible for creating service instances
 * for the TopCard game. This class implements the TopCardMarker interface.
 * <p>
 *  Author: Rajesh Rajchal
 *  Date: 11/21/2024
 */
public class ServiceFactory implements TopCardMarker {

    /**
     * Creates an instance of the specified service class. If a list of players is provided,
     * it creates an instance of GameService. Otherwise, it creates an instance of PlayerService
     * or CardService.
     *
     * @param serviceClass the class of the service to create
     * @param args         the optional arguments (e.g., list of players for GameService)
     * @param <T>          the type of the service to create
     * @return a new instance of the specified service
     */
    public static <T> T createService(Class<T> serviceClass, Object... args) {
        try {
            if (args == null || args.length == 0) {
                // Create an instance of PlayerService or CardService
                return serviceClass.getDeclaredConstructor().newInstance();
            } else {
                // Create an instance of GameService with players
                return serviceClass.getDeclaredConstructor(List.class).newInstance(args);
            }
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new TopCardException(e);
        }
    }
}

