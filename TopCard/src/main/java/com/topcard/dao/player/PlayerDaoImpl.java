package com.topcard.dao.player;

import com.topcard.domain.Player;
import com.topcard.util.HibernateUtil;
import com.topcard.exceptions.TopCardException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
//import org.hibernate.query.Query; // For HQL queries (Hibernate Query Language)

import java.util.List;
import java.util.Optional;

@Repository
public class PlayerDaoImpl implements IPlayerDao {

    private static final Logger logger = LogManager.getLogger(PlayerDaoImpl.class);

    @Override
    public Player addPlayer(Player player) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(player); // Persist (save) the player object
            transaction.commit();
            logger.info("Player added to DB: " + player.getUsername() + " with ID: " + player.getPlayerId());
            return player;
        } catch (Exception e) {
            if (transaction != null) {
                logger.error("Error adding player to database: " + e.getMessage());
                transaction.rollback();
            }
            logger.error("Database error adding player: " + player.getUsername(), e);
            throw new TopCardException("Error adding player to database: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Player> getPlayerById(int playerId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // get() method retrieves an entity by its primary key
            Player player = session.get(Player.class, playerId);
            if (player != null) {
                logger.debug("Player found by ID: " + playerId);
                return Optional.of(player);
            }
            logger.debug("Player not found by ID: " + playerId);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Database error getting player by ID: " + playerId, e);
            throw new TopCardException("Error retrieving player by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Player> getPlayerByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Player> criteria = builder.createQuery(Player.class);
            Root<Player> root = criteria.from(Player.class);
            criteria.select(root).where(builder.equal(root.get("username"), username));
            Player player = session.createQuery(criteria).uniqueResult();

            /*
            // HQL query to find a player by username
            Query<Player> query = session.createQuery("FROM Player WHERE username = :username", Player.class);
            query.setParameter("username", username);
            Player player = query.uniqueResult(); //
            */

            if (player != null) {
                logger.debug("Player found by username: " + username);
                return Optional.of(player);
            }
            logger.debug("Player not found by username: " + username);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Database error getting player by username: " + username, e);
            throw new TopCardException("Error retrieving player by username: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updatePlayer(Player player) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(player);  // update() function is deprecated
            transaction.commit();
            logger.info("Player updated in DB: " + player.getUsername());
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Database error updating player: " + player.getUsername(), e);
            throw new TopCardException("Error updating player in database: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deletePlayer(int playerId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            // First, load the player to be deleted.
            Player player = session.get(Player.class, playerId);
            if (player != null) {
                session.remove(player); // delete() function is deprecated
                transaction.commit();
                logger.info("Player deleted from DB with ID: " + playerId);
                return true;
            }
            logger.warn("Failed to delete player from DB with ID: " + playerId + ". Player not found.");
            return false;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Database error deleting player with ID: " + playerId, e);
            throw new TopCardException("Error deleting player from database: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Player> getAllPlayers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Player> criteria = builder.createQuery(Player.class);
            Root<Player> root = criteria.from(Player.class);
            criteria.select(root);
            List<Player> players = session.createQuery(criteria).getResultList();
            /*
            // HQL to select all players
            List<Player> players = session.createQuery("FROM Player", Player.class).list();
            */
            logger.info("Retrieved " + players.size() + " players from DB.");
            return players;
        } catch (Exception e) {
            logger.error("Database error getting all players.", e);
            throw new TopCardException("Error retrieving all players from database: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteAllPlayersData() {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaDelete<Player> criteriaDelete = builder.createCriteriaDelete(Player.class);
            criteriaDelete.from(Player.class); // Deletes all player
            int affectedRows = session.createMutationQuery(criteriaDelete).executeUpdate();
            /*
            // HQL DELETE statement
            Query query = session.createQuery("DELETE FROM Player");
            int affectedRows = query.executeUpdate();
             */
            transaction.commit();
            logger.info("Deleted " + affectedRows + " players from DB.");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Database error deleting all players.", e);
            throw new TopCardException("Error deleting all players from database: " + e.getMessage(), e);
        }
    }
}