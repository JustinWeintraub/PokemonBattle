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
    imageSize = new PVector(width*.4, height*.4);
    pokemonImg.resize(int(imageSize.x), int(imageSize.y));
    maxStats.set("HP",str(int(data.getString("HP"))*2+5));
    maxStats.set("Attack",str(int(data.getString("Attack"))*2+5));
    maxStats.set("Defense",str(int(data.getString("Defense"))*2+5));
    maxStats.set("Sp. Atk", str(int(data.getString("Sp. Atk"))*2+5));
    maxStats.set("Sp. Def", str(int(data.getString("Sp. Def"))*2+5));
    maxStats.set("Speed", str(int(data.getString("Speed"))*2+5));
    currentStats=maxStats.copy();    
    type =  split(data.getString("Typing"),' ');
  }
  
  ArrayList<String> determineDamage(Move move, Pokemon pokemon2){ //pokemon2 is the one attacking with a move
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
    float critChance=.0417;
    if(move.name.equals("Slash"))critChance=.1215;
    if(random(1)<critChance){
      damageModifier*=2;
      text.add("It's a critical hit!");
    }
    if(move.type[0]==type[0] || (type.length>1&& move.type[0]==type[1]))damageModifier*=1.5;
    damageModifier*=random(.85,1);
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
    
    float damage= ((2*100/5+2)*move.power*float(pokemon2.currentStats.get(attackSearch))/float(currentStats.get(defenseSearch))/50+2)*damageModifier;
    text.add(str(damage));
    return(text);
  }
  
  void update(){
    display(background);
    updateHP();
  }
  void display(Background background){
  }
  void attack(){
  }
  void updateHP(){ //Every frame this pokemon takes damage as long as it hasn't fainted and there is damageLeft
    if(damageLeft>0 && int(currentStats.get("HP"))>0){
      currentStats.set("HP", str(int(currentStats.get("HP"))-1));
      damageLeft--;
    }
    if(int(currentStats.get("HP"))==0 && fainted==false){
      fainted=true;
      background.textLag.add(name + " has fainted!");
      background.action="Fainted";
    }
  }
  void updateDamage(int damage){ //If a pokemon has taken damage, this function is called
    damageLeft+=damage;
  }
}
