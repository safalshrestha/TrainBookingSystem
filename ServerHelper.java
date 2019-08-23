 

import DB.*;
import Public.*;
import UI.ServerAction;
import UI.UserBookingAreaPanel;
import UI.UserCancelAreaPanel;
import UI.UserListAreaPanel;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Server helper, hold all the methods server provide and parameters
 *
 * @author <Yiyue Wang(Eden)>
 */
public class ServerHelper
{
    private List<String> m_onlineUserNames = new ArrayList<>();
    private List<String> m_userNames_Book = new ArrayList<>(10);
    private List<String> m_userNames_Cancel = new ArrayList<>(10);
    private List<History> m_userBookHistories = new ArrayList<>(10);
    private List<History> m_userCancelHistories = new ArrayList<>(10);
    private TrainDao m_trainDao = new TrainDao();
    private HistoryDao m_historyDao = new HistoryDao();
    private Train m_train;
    private Journey m_journey;
    private List<Train> m_allTrainTickets, m_searchTickets, m_searchTicketsOneWay, m_searchJourneyTickets;
    private List<Journey> m_allTrainJourneys;
    private List<History> m_histories = new ArrayList<>();
    private List<String> m_allTrainDepartStops, m_allTrainArriveStops;
    private final static List<Seat> currentSeatPlan = Arrays.asList(
            new Seat("01A", false), new Seat("01B", false), new Seat("01C", false),
            new Seat("02A", false), new Seat("02B", false), new Seat("02C", false),
            new Seat("03A", false), new Seat("03B", false), new Seat("03C", false),
            new Seat("04A", false), new Seat("04B", false), new Seat("04C", false),
            new Seat("05A", false), new Seat("05B", false), new Seat("05C", false),
            new Seat("06A", false), new Seat("06B", false), new Seat("06C", false),
            new Seat("07A", false), new Seat("07B", false), new Seat("07C", false),
            new Seat("08A", false), new Seat("08B", false), new Seat("08C", false),
            new Seat("09A", false), new Seat("09B", false), new Seat("09C", false),
            new Seat("10A", false), new Seat("10B", false), new Seat("10C", false),
            new Seat("11A", false), new Seat("11B", false), new Seat("11C", false),
            new Seat("12A", false), new Seat("12B", false), new Seat("12C", false),
            new Seat("13A", false), new Seat("13B", false), new Seat("13C", false));

    /**
     * Display all trains
     *
     * @return
     */
    public List<Train> getAllTrainTickets()
    {
        try
        {
            m_allTrainTickets = m_trainDao.findAllTrains();
        } catch (DaoException ex)
        {
            Logger.getLogger(ServerHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return m_allTrainTickets;
    }

    /**
     * Find train by trainID
     *
     * @param trainID
     * @return
     */
    public Train getTrainTicketsByID(int trainID)
    {
        try
        {
            m_train = m_trainDao.findTrainsByID(trainID);
        } catch (DaoException ex)
        {
            Logger.getLogger(ServerHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return m_train;
    }

    public List<Train> getTrainTicketsByFrom(String from)
    {
        try
        {
            m_searchTicketsOneWay = m_trainDao.findTrainsByFrom(from);
        } catch (DaoException ex)
        {
            Logger.getLogger(ServerHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return m_searchTicketsOneWay;
    }

    public List<Train> getTrainTicketsByFromTo(String from, String to)
    {
        try
        {
            m_searchTickets = m_trainDao.findTrainsByFromTo(from, to);
        } catch (DaoException ex)
        {
            Logger.getLogger(ServerHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return m_searchTickets;
    }

    /**
     * Get train journey by from and to locations, calculate max three change
     * stops for now, future will fix this.
     *
     * @param from
     * @param to
     * @return
     */
    public synchronized List<Journey> getTrainJourneyByFromTo(String from, String to)
    {
        m_allTrainJourneys = new ArrayList<>();
        String change; // at least one change
        int firstTrainArTime, secondTrainArTime, thirdTrainArTime;
        int secondTrainDpTime, thirdTrainDpTime;
        String secondDpName;
        m_searchJourneyTickets = new ArrayList<>();
        Train t1, t2, t3;

        // First round check depart from origin location
        for (Iterator<Train> i = getTrainTicketsByFrom(from).iterator(); i.hasNext();)
        {
            t1 = i.next();
            firstTrainArTime = Integer.parseInt(t1.getArriveAt());
            // Second round check depart from origin's arrive location
            for (Iterator<Train> j = getTrainTicketsByFrom(t1.getArriveTo()).iterator(); j.hasNext();)
            {
                t2 = j.next();
                // Check depart location of t2
                if (t2.getArriveTo() == null ? from != null : !t2.getArriveTo().equals(from))
                {
                    secondTrainDpTime = Integer.parseInt(t2.getDepartAt());
                    secondTrainArTime = Integer.parseInt(t2.getArriveAt());
                    secondDpName = t2.getDepartFrom();
                    // Compare time possibility
                    if (firstTrainArTime < secondTrainDpTime)
                    {
                        // Check final destination with t2 arrive
                        if (t2.getArriveTo() == null ? to != null : !t2.getArriveTo().equals(to))
                        {
                            // Third round check from origin's arrive location as depart to final destination 'to'
                            for (Iterator<Train> k = getTrainTicketsByFrom(t2.getArriveTo()).iterator(); k.hasNext();)
                            {
                                t3 = k.next();
                                thirdTrainDpTime = Integer.parseInt(t3.getDepartAt());
                                thirdTrainArTime = Integer.parseInt(t3.getArriveAt());
                                // Compare time possibility
                                if (secondTrainArTime < thirdTrainDpTime)
                                {
                                    if ((t3.getArriveTo() == null ? to != null : !t3.getArriveTo().equals(to))
                                            || (t3.getArriveTo() == null ? secondDpName == null : secondDpName.equals(t3.getArriveTo()))
                                            || (t3.getArriveTo() == null ? from == null : t3.getArriveTo().equals(from)))
                                    {
                                    } else
                                    {
                                        // Found in third round
                                        change = "2";
                                        m_searchJourneyTickets.add(t1);
                                        m_searchJourneyTickets.add(t2);
                                        m_searchJourneyTickets.add(t3);
                                        m_journey = new Journey(change, t1.getDepartAt(), String.valueOf(thirdTrainArTime), m_searchJourneyTickets);
                                        m_allTrainJourneys.add(m_journey);
                                        m_searchJourneyTickets = new ArrayList<>();
                                    } // End if
                                } // End if
                            } // End for loop
                        } else
                        {
                            // Found in second round
                            change = "1";
                            m_searchJourneyTickets.add(t1);
                            m_searchJourneyTickets.add(t2);
                            m_journey = new Journey(change, t1.getDepartAt(), String.valueOf(secondTrainArTime), m_searchJourneyTickets);
                            m_allTrainJourneys.add(m_journey);
                            m_searchJourneyTickets = new ArrayList<>();
                        } // End if
                    } // End if
                } // End if
            } // End for loop
        }
        return m_allTrainJourneys;
    }

    public List<String> getAllTrainDepartStops()
    {
        try
        {
            m_allTrainDepartStops = m_trainDao.findAllDepartNames();
        } catch (DaoException ex)
        {
            Logger.getLogger(ServerHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return m_allTrainDepartStops;
    }

    public List<String> getAllTrainArriveStops()
    {
        try
        {
            m_allTrainArriveStops = m_trainDao.findAllArriveNames();
        } catch (DaoException ex)
        {
            Logger.getLogger(ServerHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return m_allTrainArriveStops;
    }

    public List<History> getUserHistories(String username)
    {
        try
        {
            m_histories = m_historyDao.findAllHistoriesByUsername(username);
        } catch (DaoException ex)
        {
            Logger.getLogger(ServerHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return m_histories;
    }

    public synchronized void updateUserBookingUI(History history)
    {
        // Modify
        m_userNames_Book.add(history.getUsername());
        m_userBookHistories.add(history);
        // Update panel
        updateUserBooingPanel();
    }

    /**
     * Update user booking panel
     */
    private void updateUserBooingPanel()
    {
        UserBookingAreaPanel.g_userNames = m_userNames_Book;
        UserBookingAreaPanel.g_histories = m_userBookHistories;
        UserBookingAreaPanel.repaintPanel();
    }

    public synchronized void updateUserCancelUI(History history)
    {
        m_userNames_Cancel.add(history.getUsername());
        m_userCancelHistories.add(history);
        updateUserCancelPanel();
    }

    private void updateUserCancelPanel()
    {
        UserCancelAreaPanel.g_userNames = m_userNames_Cancel;
        UserCancelAreaPanel.g_histories = m_userCancelHistories;
        UserCancelAreaPanel.repaintPanel();
    }

    public static List<Seat> getCurrentSeatPlan()
    {
        return currentSeatPlan;
    }

    /**
     * Get current booked seats by trainID
     *
     * @param trainID
     * @return
     */
    public synchronized List<String> getBookedSeatsByTrain(int trainID)
    {
        List<String> seats = new ArrayList<>();
        try
        {
            seats = m_historyDao.findBookedSeatsByTrainID(trainID);
        } catch (DaoException ex)
        {
            Logger.getLogger(ServerHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return seats;
    }

    /**
     * Add train into database
     *
     * @param train
     * @
     */
    public synchronized void addTrain(Train train)
    {
        try
        {
            m_trainDao.addTrain(train);
        } catch (Exception e)
        {
        }
    }

    /**
     * Add history into database
     *
     * @param history
     */
    public synchronized void addHistory(History history)
    {
        try
        {
            m_historyDao.addHistory(history);
        } catch (DaoException ex)
        {
            Logger.getLogger(ServerHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Delete history from database
     *
     * @param historyID
     */
    public synchronized void deleteHistory(int historyID)
    {
        try
        {
            m_historyDao.deleteHistoryByID(historyID);
        } catch (DaoException ex)
        {
            Logger.getLogger(ServerHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Login a client by clientName
     *
     * @param clientName
     * @return
     */
    public synchronized boolean login(String clientName)
    {
        try
        {
            // Save into a online list.
            addOnline(clientName);
        } catch (Exception e)
        {
        }
        return true;
    }

    /**
     * Check if user is logged in or not
     *
     * @param clientName
     * @return
     */
    public synchronized boolean isUserLogin(String clientName)
    {
        boolean isLogin = false;
        String username;
        try
        {
            for (int i = 0; i < m_onlineUserNames.size(); i++)
            {
                username = m_onlineUserNames.get(i);
                if (username.equals(clientName))
                {
                    isLogin = true;
                }
            }
        } catch (Exception e)
        {
        }
        return isLogin;
    }

    /**
     * Logout a client
     *
     * @param clientName
     * @return
     */
    public synchronized boolean logout(String clientName)
    {
        try
        {
            // Remove from online list.
            deleteClient(clientName);
        } catch (Exception e)
        {
        }
        return true;
    }

    /**
     * Get current online number of users
     *
     * @return
     */
    public synchronized String getNumOnlineUser()
    {
        return String.valueOf(m_onlineUserNames.size());
    }

    /**
     * Add an online client into online user list by clientName
     *
     * @param clientName
     */
    public synchronized void addOnline(String clientName)
    {
        m_onlineUserNames.add(clientName);
        noticeServerUI();
    }

    /**
     * Remove an online client from online user list by clientName
     *
     * @param clientName
     */
    public synchronized void deleteClient(String clientName)
    {
        m_onlineUserNames.remove(clientName);
        noticeServerUI();
    }

    /**
     * Update server UI
     */
    public void noticeServerUI()
    {
        UserListAreaPanel.g_userList = m_onlineUserNames;
        ServerAction.g_numOLUsers = m_onlineUserNames.size();
        ServerAction.updateNumOLUsers();
        UserListAreaPanel.repaintPanel();
    }

    /**
     * Reset all data in database
     */
    public void resetAllData()
    {
        CreateTables action = new CreateTables();
        try
        {
            action.dropCreateTables();
        } catch (DaoException ex)
        {
            Logger.getLogger(ServerHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
