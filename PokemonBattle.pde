Background background;
Player player;
Opponent opponent;

Table typeChart, moveList;

void setup(){
  size(400,400);
  
  background=new Background();
  
  typeChart= loadTable("data/Type.csv", "header"); //loading csv files with data
  moveList = loadTable("data/Moves.csv","header");
  Table table = loadTable("data/Pokemon.csv", "header");
  
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
void draw(){
  background.display(player, opponent);
  player.update();
  opponent.update();

}

void updateGame(){
  //Essentially, this conducts the attack phase
  //Gets player and opponent's moves, determines order 
  //and then calls for turn to be conducted
  
  Move playerMove = player.moves.get(background.cursor[0]+background.cursor[1]*2);
  Move opponentMove=opponent.moves.get(int(random(4)));
  
  ArrayList<String> playerAttackText=opponent.determineDamage(playerMove, player);
  int playerAttackDamage=int(playerAttackText.get(playerAttackText.size()-1));
  playerAttackText.remove(playerAttackText.size()-1);
  
  ArrayList<String> opponentAttackText=player.determineDamage(opponentMove, opponent);
  int opponentAttackDamage=int(opponentAttackText.get(opponentAttackText.size()-1));
  opponentAttackText.remove(opponentAttackText.size()-1);
  
  if(int(player.currentStats.get("Speed"))>int(opponent.currentStats.get("Speed"))){
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
boolean checkCurrentTurn(){
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


void keyPressed(){
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
