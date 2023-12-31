import java.util.*;

public class BoardEvent implements Event {
  public final Passenger p; public final Train t; public final Station s;
  public BoardEvent(Passenger p, Train t, Station s) {
    this.p = p; this.t = t; this.s = s;
  }
  public boolean equals(Object o) {
    if (o instanceof BoardEvent e) {
      return p.equals(e.p) && t.equals(e.t) && s.equals(e.s);
    }
    return false;
  }
  public int hashCode() {
    return Objects.hash(p, t, s);
  }
  public String toString() {
    return "Passenger " + p + " boards " + t + " at " + s;
  }
  public List<String> toStringList() {
    return List.of(p.toString(), t.toString(), s.toString());
  }
  public void replayAndCheck(MBTA mbta) {
    mbta.boardPass(mbta, p, s, t);
    // System.out.println("Board#: " + this.toString());
    // System.out.println("Board#TrainAndStations: " + mbta.trainAndStationsKVP);
    // System.out.println("Board#PassengersAndJourney: " + mbta.passengerAndStationsKVP);
    // System.out.println("Board#BoardedPassengers: " + mbta.trainToBoardedPassengers);
    // Map<Train, LinkedList<Station>> trainLine = mbta.trainAndStationsKVP;
    // /* ensure that the train exist */
    // if (trainLine.containsKey(this.t) || this.t != null)
    // {
    //   LinkedList<Station> lineStations = trainLine.get(this.t);
    //   /* ensure the train line has stations */
    //   if (lineStations != null)
    //   {
    //     /* ensure that the train stations contains the station the passenger is boarding from */
    //     if (lineStations.contains(this.s))
    //     {
    //       /* ensure that the current station of the train is the station the passenger is boarding from */
    //       if (lineStations.getFirst().equals(this.s))
    //       {
    //         LinkedList<Passenger> boardPassengers = mbta.trainToBoardedPassengers.get(this.t);

    //         /* create a new list to store boarded passenger if there doesn't already exist a list */
    //         if (boardPassengers == null)
    //         {
    //           boardPassengers = new LinkedList<>();
    //           LinkedList<Station> givenPassengerJourney = mbta.passengerAndStationsKVP.get(this.p);
    //           // System.out.println("Board#Pass is: " + this.p);
    //           // System.out.println("Board#Pass and Station: " + givenPassengerJourney);
    //           /* ensure that the journey to the given station for the given passenger has been initialize */
    //           if (givenPassengerJourney.contains(this.s))
    //           {
    //             boardPassengers.add(this.p);

    //             if (mbta.stationAndWaitingPassenger.get(this.s) != null)
    //             {
    //               mbta.stationAndWaitingPassenger.get(this.s).remove(this.p);
    //             }
                
    //             // System.out.println("Remove sta: " + this.s);
    //             /* remove the station they boarded from, from their journey */
    //             givenPassengerJourney.remove(this.s);
    //             mbta.trainToBoardedPassengers.put(this.t, boardPassengers);
    //             mbta.trainAndIfPassengerHasBeenBoarded.put(this.t, true);
    //             System.out.println("Pass Station List after board: " + mbta.stationAndWaitingPassenger.get(this.s));
    //           }
    //           else
    //           {
    //             throw new IllegalArgumentException("111111 Error in {BoardEvent#replayAndCheck}: Journey to station {" + this.s + "} for passenger {" + this.p.toString() + "} has not yet been initialized.");
    //           }
    //         }
    //         else
    //         {
    //           // /* ensure that all passengers from all station that is willing to board the train have uniqe name e.g if John board red train from station1, John cannot board red train from any other stations */
    //             LinkedList<Station> givenPassengerJourney = mbta.passengerAndStationsKVP.get(this.p);
    //             // System.out.println("Board#Has Pass: " + this.p);
    //             // System.out.println("Board#Has Pass and Station: " + givenPassengerJourney);
    //             /* ensure that the journey to the given station for the given passenger has been initialize */
    //             if (givenPassengerJourney.contains(this.s))
    //             {
    //               if (mbta.stationAndWaitingPassenger.get(this.s) != null)
    //               {
    //                 mbta.stationAndWaitingPassenger.get(this.s).remove(this.p);
    //               }
    //               boardPassengers.add(this.p);
    //               // mbta.stationAndWaitingPassenger.get(this.s).remove(this.p);
    //               givenPassengerJourney.remove(this.s);
    //               mbta.trainToBoardedPassengers.put(this.t, boardPassengers);
    //               mbta.trainAndIfPassengerHasBeenBoarded.put(this.t, true);
    //             }
    //             else
    //             {
    //               throw new IllegalArgumentException("222222222 Error in {BoardEvent#replayAndCheck}: Journey to station {" + this.s + "} for passenger {" + this.p.toString() + "} has not yet been initialized.");
    //             }
    //         }
    //       }
    //       else
    //       {
    //         throw new IllegalArgumentException("Error in {BoardEvent#replayAndCheck}: Unable to board from station {" +this.s.toString() + "} as current station of the train is {" + lineStations.getFirst() + "}.");
    //       }
    //     }
    //     else
    //     {
    //       throw new IllegalArgumentException("Error in {BoardEvent#replayAndCheck}: Train {" + this.t + "} does not contains the station {" + this.s.toString() + "}.");
    //     }
    //   }
    //   else
    //   {
    //     throw new IllegalArgumentException("Error in {BoardEvent#replayAndCheck}: Unable to board onto Train {" + this.t.toString() + "}.");
    //   }
    // }
    // else
    // {
    //   throw new IllegalArgumentException("Error in {BoardEvent#replayAndCheck}: Train {" + this.t.toString() + "} does not exist.");
    // }
  }
}
