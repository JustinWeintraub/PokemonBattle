import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class PokemonBattle extends PApplet {

Background background;
Player player;
Opponent opponent;

Table typeChart, moveList;

public void setup(){
  
  
  background=new Background();
  
  typeChart= loadTable("Type.csv", "header"); //loading csv files with data
  moveList = loadTable("Moves.csv","header");
  Table table = loadTable("Pokemon.csv", "header");
  
  for (TableRow row : table.rows()) {
    String name = row.getString("Name");
    if(name.equals("Charizard")){
      player = new Player("CHARIZARD",loadImage("charizard.png"), row);
      player.moves.add(new Move("Flamethrower"));
      player.moves.add(new Move("Slash"));
      player.moves.add(new Move("Dragon Claw"));
      player.moves.add(new Move("SolarBeam"));
    }
    if(name.equals("Blastoise")){
      opponent = new Opponent("BLASTOISE",loadImage("blastoise.png"),row);
      opponent.moves.add(new Move("Water Pulse"));
      opponent.moves.add(new Move("Ice Beam"));
      opponent.moves.add(new Move("Earthquake"));
      opponent.moves.add(new Move("Mega Kick"));
    }

  }
 
}
public void draw(){
  background.display(player, opponent);
  player.update();
  opponent.update();

}

public void updateGame(){
  //Essentially, this conducts the attack phase
  //Gets player and opponent's moves, determines order 
  //and then calls for turn to be conducted
  
  Move playerMove = player.moves.get(background.cursor[0]+background.cursor[1]*2);
  Move opponentMove=opponent.moves.get(PApplet.parseInt(random(4)));
  
  ArrayList<String> playerAttackText=opponent.determineDamage(playerMove, player);
  int playerAttackDamage=PApplet.parseInt(playerAttackText.get(playerAttackText.size()-1));
  playerAttackText.remove(playerAttackText.size()-1);
  
  ArrayList<String> opponentAttackText=player.determineDamage(opponentMove, opponent);
  int opponentAttackDamage=PApplet.parseInt(opponentAttackText.get(opponentAttackText.size()-1));
  opponentAttackText.remove(opponentAttackText.size()-1);
  
  if(PApplet.parseInt(player.currentStats.get("Speed"))>PApplet.parseInt(opponent.currentStats.get("Speed"))){
    player.turn=true;
  }
  else {
    player.turn=false;
  }
  
  player.attackDamage=playerAttackDamage;
  player.attackText=playerAttackText;
  opponent.attackDamage=opponentAttackDamage;
  opponent.attackText=opponentAttackText;
  background.changeText();

}

//Makes battle turn based, only one can go at a time
//Activates whenever text for one pokemon's attacks end
//or start of the Pokemon attacking
String currentTurn="nobody";
String nextTurn="nobody";
public boolean checkCurrentTurn(){
  if(nextTurn.equals("player") && currentTurn.equals("opponent")){
      nextTurn="nobody";
      currentTurn="player";
      player.attack();
    }
    else if(nextTurn.equals("opponent") && currentTurn.equals("player")){
      nextTurn="nobody";
      currentTurn="opponent";
      opponent.attack();
    }
    else if(player.turn==true && currentTurn.equals("nobody")){
      nextTurn="opponent";
      currentTurn="player";
      player.attack();
    }
    else if(opponent.turn==true && currentTurn.equals("nobody")){
      nextTurn="player";
      currentTurn="opponent";
      opponent.attack();
    }
    else if(nextTurn.equals("nobody")){currentTurn="nobody"; return true;}
    return false;
}


public void keyPressed(){
    if(key == ENTER || key=='Z'){
        if(background.action=="Moves"){
          background.action="Attack";    
          updateGame();
        }
        else if(!background.currentTextPart.equals(background.currentText)){
          background.currentTextPart=background.currentText;
          background.positionInText=background.currentText.length();
        }
        else {background.changeText();}
      }
    if(key==CODED){
      if(background.cursorChange){
      if(keyCode == UP || keyCode==DOWN)background.cursor[1]=(background.cursor[1]+1)%2;
      if(keyCode == LEFT || keyCode==RIGHT)background.cursor[0]=(background.cursor[0]+1)%2;
      }
    }
  
}
class Background{
  float textWeight, boxTextInitX;
  PVector textInit,  textLength, boxLength;
  float boxTextLength;
  String action = "Moves"; //possible actions: Moves, Attackl Fainted
  int[] cursor = {0,0};
  boolean cursorChange=true;
  String currentText="", currentTextPart ="";
  int positionInText;
  PFont font;
  ArrayList<String> textLag = new ArrayList<String>();
  Background(){
      textWeight=width*.01f;
      textInit= new PVector(width*.05f, height*.75f);
      textLength= new PVector(width*.9f, height*.2f);
      boxLength = new PVector(width*.4f, height*.1f);
      boxTextInitX = width*.4f/20;
      boxTextLength=textLength.y/12;
      font =createFont("PKMN RBYGSC.ttf", 32);
  }
  public void display(Player player, Opponent opponent){
      background(240);
      textBox();
      if(action=="Attack" || action=="Fainted"){
        if(currentText!="")drawText();
      }
      if(action=="Moves"){
        actions();
      }
      playerInfo(player);
      opponentInfo(opponent);
  }
  
  public void textBox(){
    //creating text box display 
      fill(240);
      stroke(185);
      strokeWeight(textWeight);
      translate(textInit.x, textInit.y);
      rect(0, 0, textLength.x, textLength.y);
      
      //creating shadow
      stroke(50);
      strokeWeight(textWeight/2);
      line(0, textWeight/2, textLength.x, textWeight/2);
      line(0, textLength.y+textWeight/2, textLength.x, textLength.y+textWeight/2);
      
      //creating wireframe
      stroke(240);
      strokeWeight(textWeight/4);
      line(0, 0, textLength.x, 0);
      line(0, textLength.y, textLength.x, textLength.y);
      line(0, 0, 0, textLength.y);
      line(textLength.x, 0, textLength.x, textLength.y);
      
      //creating pokeballs on corners
      stroke(185);
      strokeWeight(textWeight/4);
      for(float y=0; y<=textLength.y; y+=textLength.y){
        for(float x=0; x<=textLength.x; x+=textLength.x){
          fill(255);
          arc(x, y, 10, 10, 0, PI, OPEN);
          fill(105);
          arc(x, y, 10, 10, PI, 2*PI, OPEN);
        }
      }
      translate(-textInit.x, -textInit.y);
  }
  
  public void playerInfo(Player player){
    fill(0);
    stroke(0);
    strokeWeight(textWeight/2);
    translate(width/2 - textWeight*2,height/2);
    line(0, boxLength.y*2, boxLength.x,boxLength.y*2);
    triangle(0, boxLength.y*2, boxTextInitX, boxLength.y*2, boxTextInitX, boxLength.y*2 - boxLength.y*.1f);
    line(boxLength.x, boxLength.y*2,  boxLength.x, boxLength.y);
    
    textFont(font);
    textSize(boxTextLength);
    text("HP:", boxTextInitX, boxLength.y);
    textSize(boxTextLength*2.5f);
    text(player.name, boxTextInitX, boxLength.y-boxTextLength*5);
    text(":L100", boxTextInitX+boxLength.x/4, boxLength.y-boxTextLength*2.5f);
    noFill();
    rect(boxTextInitX+boxTextLength*3, boxLength.y - boxTextLength/1.25f, boxLength.x-boxTextInitX-boxTextLength*4, boxTextLength, 5,5,5,5);
    fill(105);
    rect(boxTextInitX+boxTextLength*3, boxLength.y - boxTextLength/1.25f, (boxLength.x-boxTextInitX-boxTextLength*4)*PApplet.parseInt(player.currentStats.get("HP"))/PApplet.parseInt(player.maxStats.get("HP")), boxTextLength, 5,5,5,5);
    fill(0);
    text(PApplet.parseInt(player.currentStats.get("HP"))+" / "+PApplet.parseInt(player.maxStats.get("HP")), boxTextInitX+boxLength.x/8, boxLength.y+height*.0625f);
        
    translate(-(width/2 - textWeight*2),-height/2);
  }
  
  public void opponentInfo(Opponent opponent){
    fill(0);
    stroke(0);
    strokeWeight(textWeight/2);
    translate(textWeight*8,0);
    line(0, boxLength.y*2, boxLength.x,boxLength.y*2);
    triangle(boxLength.x-boxTextInitX, boxLength.y*2, boxLength.x, boxLength.y*2, boxLength.x-boxTextInitX, boxLength.y*2 - boxLength.y*.1f);
    line(0, boxLength.y*2,  0, boxLength.y*1.25f);
    
    textFont(font);
    textSize(boxTextLength);
    text("HP:", boxTextInitX, boxLength.y*1.75f);
    
    textSize(boxTextLength*2.5f);
    text(opponent.name, boxTextInitX, boxLength.y*1.75f-boxTextLength*5);
    text(":L100", boxTextInitX+boxLength.x/4, boxLength.y*1.75f-boxTextLength*2.5f);

    noFill();
    rect(boxTextInitX+boxTextLength*3, boxLength.y*1.75f - boxTextLength/1.25f, boxLength.x-boxTextInitX-boxTextLength*4, boxTextLength, 5,5,5,5);
    fill(205);
    rect(boxTextInitX+boxTextLength*3, boxLength.y*1.75f - boxTextLength/1.25f, (boxLength.x-boxTextInitX-boxTextLength*4)*PApplet.parseInt(opponent.currentStats.get("HP"))/PApplet.parseInt(opponent.maxStats.get("HP")), boxTextLength, 5,5,5,5);
    
    translate(-textWeight*8,0);
  }
  public void drawText(){
    ArrayList<String> textShortened = new ArrayList<String>();
    if(currentTextPart.length()>20){
      int breakPoint=20;
      for(int i=20; i>0; i--){
        if(currentTextPart.charAt(i)==' '){
          breakPoint=i;
          break;
        }
      }
      textShortened.add(currentTextPart.substring(0,breakPoint));
      textShortened.add(currentTextPart.substring(breakPoint));
    }
    else {
      textShortened.add(currentTextPart);
      textShortened.add("");
    }
    translate(textInit.x, textInit.y);
    fill(0);
    stroke(0);
    strokeWeight(1);
    textFont(font);
    textSize(textLength.y/4.5f);
    text(textShortened.get(0), textWeight*2,textLength.y/3+textWeight*2); 
    text(textShortened.get(1), textWeight*2,textLength.y/3+textWeight*10); 
    if(positionInText<currentText.length()){
      currentTextPart+=currentText.charAt(positionInText);
    }
    positionInText++;
    if(positionInText>currentText.length()*5 && (action!="Fainted"||textLag.size()>0))
    {
      changeText();
    }
    translate(-textInit.x, -textInit.y);
  }
  
  public void actions(){
    fill(0);
    stroke(0);
    strokeWeight(1);
    textFont(font);
    textSize(textLength.y/6);
    text(player.moves.get(0).name, textInit.x +width*.05f, textInit.y+height*.075f);
    text(player.moves.get(1).name, textInit.x +width*(.05f+textLength.x/(2*width)), textInit.y+height*.075f);
    text(player.moves.get(2).name, textInit.x +width*.05f, textInit.y+textLength.y-height*.05f);
    text(player.moves.get(3).name, textInit.x +width*(.05f+textLength.x/(2*width)), textInit.y+textLength.y-height*.05f);
    
    if(frameCount%60<15 || frameCount%60>20){
    triangle(textInit.x+width*.01f + cursor[0]*width*textLength.x/(2*width), cursor[1]*height*.075f+textInit.y +height*.045f, 
             textInit.x+width*.01f + cursor[0]*width*textLength.x/(2*width), cursor[1]*height*.075f+textInit.y+textLength.y/6 +height*.05f, 
             textInit.x+width*.035f + cursor[0]*width*textLength.x/(2*width), cursor[1]*height*.075f+textInit.y+textLength.y/12 +height*.0475f);
    }
  }
  
  //Text goes through a buffer, chaniging text based on what's still in the buffer
  //This also changes phases and goes based off of turn order
  public void changeText(){
    String text="";
    boolean refresh=false;
    if(textLag.size()==0 && player.fainted==false && opponent.fainted==false)refresh =checkCurrentTurn();
    if(textLag.size()>0){
      text=textLag.get(0);
      textLag.remove(0);
    }
    else{
    if(refresh){
      background.action="Moves";
      if(player.moves.get(cursor[0]+cursor[1]*2).charging==true)cursorChange=false;
      else cursorChange=true;
    }
    }
    currentText=text;
    currentTextPart="";
    positionInText=0;
  }
}
class Move{
  String name, category;
  String[] type;
  float power, accuracy, pp;
  boolean charging=false;
  Move(String _name){
    name=_name;
      for (TableRow row : moveList.rows()) {
        String columnName = row.getString("Name");
        if(columnName.equals(name)){
          category = row.getString("Type");
          type = new String[]{row.getString("Type")};
          power = PApplet.parseInt(row.getString("Power"));
          accuracy = PApplet.parseFloat(row.getString("Accuracy").substring(0,row.getString("Accuracy").length()-1))/100;
          pp = PApplet.parseInt(row.getString("PP"));
        }
      }
  }

}
class Opponent extends Pokemon{
  
  Opponent(String _name, PImage img, TableRow data){
    super(_name, img, data);
  }
  public void display(Background background){
    //displays image of Pokemon
    if(damageLeft%10!=1 && fainted==false)image(pokemonImg, width - imageSize.x - background.textWeight,
      background.textWeight);
  }
  public void attack(){
     //Opponent attacks player, calls text for attack
     player.updateDamage(attackDamage);
      for(int i=0; i<attackText.size(); i++){
        background.textLag.add(attackText.get(i));
      }
     turn = false;
  }
}
class Player extends Pokemon{
  
  
  Player(String _name, PImage img, TableRow data){
    super(_name, img, data);
  }
  public void display(Background background){
     //displays image of Pokemon
    if(damageLeft%10!=1 && fainted==false)image(pokemonImg, background.textInit.x,
      background.textInit.y - background.textWeight*2 - imageSize.y);
  }
  
  public void attack(){
    //Player atacks opponent, calls text for attack
    opponent.updateDamage(attackDamage);
      for(int i=0; i<attackText.size(); i++){
        background.textLag.add(attackText.get(i));
      }
    turn = false;
  }
}
class Pokemon{
  String name;
  PImage pokemonImg;
  PVector imageSize;
  
  StringDict maxStats = new StringDict();
  StringDict currentStats = new StringDict();
  
  String[] type;
  ArrayList<Move> moves = new ArrayList<Move>();
  //Move move = new Move();
  
  int damageLeft=0;
  boolean fainted = false;
  
 int attackDamage;
  ArrayList<String>attackText;
  boolean turn = false;
  
  Pokemon(String _name, PImage img, TableRow data){
    name=_name;
    pokemonImg=img;
    imageSize = new PVector(width*.4f, height*.4f);
    pokemonImg.resize(PApplet.parseInt(imageSize.x), PApplet.parseInt(imageSize.y));
    maxStats.set("HP",str(PApplet.parseInt(data.getString("HP"))*2+5));
    maxStats.set("Attack",str(PApplet.parseInt(data.getString("Attack"))*2+5));
    maxStats.set("Defense",str(PApplet.parseInt(data.getString("Defense"))*2+5));
    maxStats.set("Sp. Atk", str(PApplet.parseInt(data.getString("Sp. Atk"))*2+5));
    maxStats.set("Sp. Def", str(PApplet.parseInt(data.getString("Sp. Def"))*2+5));
    maxStats.set("Speed", str(PApplet.parseInt(data.getString("Speed"))*2+5));
    currentStats=maxStats.copy();    
    type =  split(data.getString("Typing"),' ');
  }
  
  public ArrayList<String> determineDamage(Move move, Pokemon pokemon2){ //pokemon2 is the one attacking with a move
    //calculates damage based off of Pokemon's formulas
    //returns text for attack and damage
    ArrayList<String> text = new ArrayList<String>();
    text.add(pokemon2.name+" used " + move.name + "!");
    float damageModifier = 1;
    if(random(1)<move.accuracy && (!move.name.equals("SolarBeam")||move.charging==true)){
      move.charging=false;
    damageModifier *=superModifier(type, move.type);
    if(damageModifier>=2)text.add("It's super effective!");
    if(damageModifier<1 && damageModifier>0)text.add("It's not very effective...");
    if(damageModifier==0)text.add("But it had no effect!");
    float critChance=.0417f;
    if(move.name.equals("Slash"))critChance=.1215f;
    if(random(1)<critChance){
      damageModifier*=2;
      text.add("It's a critical hit!");
    }
    if(move.type[0]==type[0] || (type.length>1&& move.type[0]==type[1]))damageModifier*=1.5f;
    damageModifier*=random(.85f,1);
    }
    else {
        if(move.name.equals("SolarBeam")){text.add(pokemon2.name + " is charging!");move.charging=true;}
        else text.add("But it missed!"); 
        damageModifier=0;
      }
    String attackSearch="Attack";
    String defenseSearch="Defense";
    if(move.category=="Special"){
      attackSearch="Sp. Atk";
      defenseSearch="Sp. Def";
    }
    
    float damage= ((2*100/5+2)*move.power*PApplet.parseFloat(pokemon2.currentStats.get(attackSearch))/PApplet.parseFloat(currentStats.get(defenseSearch))/50+2)*damageModifier;
    text.add(str(damage));
    return(text);
  }
  
  public void update(){
    display(background);
    updateHP();
  }
  public void display(Background background){
  }
  public void attack(){
  }
  public void updateHP(){ //Every frame this pokemon takes damage as long as it hasn't fainted and there is damageLeft
    if(damageLeft>0 && PApplet.parseInt(currentStats.get("HP"))>0){
      currentStats.set("HP", str(PApplet.parseInt(currentStats.get("HP"))-1));
      damageLeft--;
    }
    if(PApplet.parseInt(currentStats.get("HP"))==0 && fainted==false){
      fainted=true;
      background.textLag.add(name + " has fainted!");
      background.action="Fainted";
    }
  }
  public void updateDamage(int damage){ //If a pokemon has taken damage, this function is called
    damageLeft+=damage;
  }
}
//Determines if move is super effective using csv file
public float superModifier(String[] defendingType, String[] attackingType){
  float modifier = 1;
  for(int i=0; i<1; i++){
     for(int j=0; j<defendingType.length; j++){
       for (TableRow row : typeChart.rows()) {
         if(row.getString("Attacking").equals(attackingType[i])){
           modifier*=PApplet.parseFloat(row.getString(defendingType[j]));
         }
     }
     }
  }
  return modifier;
}
  public void settings() {  size(400,400); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "PokemonBattle" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
