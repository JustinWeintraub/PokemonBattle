class Opponent extends Pokemon{
  
  Opponent(String _name, PImage img, TableRow data){
    super(_name, img, data);
  }
  void display(Background background){
    //displays image of Pokemon
    if(damageLeft%10!=1 && fainted==false)image(pokemonImg, width - imageSize.x - background.textWeight,
      background.textWeight);
  }
  void attack(){
     //Opponent attacks player, calls text for attack
     player.updateDamage(attackDamage);
      for(int i=0; i<attackText.size(); i++){
        background.textLag.add(attackText.get(i));
      }
     turn = false;
  }
}
