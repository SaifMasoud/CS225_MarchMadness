/**
 * 
 * @author Shivanie and Tyler
 * @description 
 *       Team class holds all the information for individual teams. Their name, info, and ranking.
 *       
 */

public class Team{

  private String name;
  private String nickname;
  private String info;
  private int ranking;
  public double offensePPG;
  public double defensePPG;
  
  /**
   * Constructor
   * @param name 
   *        The name of the team
   * @param info
   * 		A short description of the team
   * @param ranking
   * 		The ranking in the team region from 1 to 16
   */
  public Team(String name, String nickname, String info, int ranking, double oPPG, double dPPG){
    this.name = name;
    this.nickname = nickname;
    this.info = info;
    this.ranking = ranking;
    offensePPG = oPPG;
    defensePPG = dPPG;
  }
    
  /**
   * 
   * @return name the name of the team
   */
  public String getName(){
    return name;
  }
  
  /**
   * 
   * @return nickname the mascot of the team
   */
  public String getNickname(){
	  return nickname;
  }
  
  /**
   * 
   * @return info a short description of the team
   */
  public String getInfo(){
    return info;
  }
  
  /**
   * 
   * @return ranking the ranking from 1 - 16
   */
  public int getRanking(){
    return ranking;
  }
  
  /**
   * 
   * @return offensePPG the average points per game for offense
   */
  public double getOffensePPG(){
    return offensePPG;
  }
  
  /**
   * 
   * @return defensePPG
   */
  public double getDefensePPG(){
    return defensePPG;
  }
  
  /**
   * 
   * @param info 
   * 		The short description of the team
   */
  public void setInfo(String info){
    this.info = info;
  }
  
  /**
   * 
   * @param newNickname the new nickname for a team
   */
  public void setNickname(String newNickname){
	  nickname = newNickname;
  }
  
  /**
   * 
   * @param ranking
   * 		The ranking from 1 to 16
   */
  public void setRanking(int ranking){
    this.ranking = ranking;
  }
  
  /**
   * 
   * @param newDefense The new points per game for defense
   */
  public void setDefense(double newDefense){
	  defensePPG = newDefense;
  }
  
  /**
   * 
   * @param newOffense the new points per game for offense.
   */
  public void setOffense(double newOffense){
	  offensePPG =  newOffense;
  }
}