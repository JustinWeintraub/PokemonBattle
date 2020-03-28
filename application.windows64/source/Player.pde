class Player extends Pokemon{
  
  
  Player(String _name, PImage img, TableRow data){
    super(_name, img, data);
  }
  void display(Background background){
     //displays image of Pokemon
    if(damageLeft%10!=1 && fainted==false)image(pokemonImg, background.textInit.x,
      background.textInit.y - background.textWeight*2 - imageSize.y);
  }
  
  void attack(){
    //Player atacks opponent, calls text for attack
    opponent.updateDamage(attackDamage);
      for(int i=0; i<attackText.size(); i++){
        background.textLag.add(attackText.get(i));
      }
    turn = false;
  }
}
