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
      textWeight=width*.01;
      textInit= new PVector(width*.05, height*.75);
      textLength= new PVector(width*.9, height*.2);
      boxLength = new PVector(width*.4, height*.1);
      boxTextInitX = width*.4/20;
      boxTextLength=textLength.y/12;
      font =createFont("PKMN RBYGSC.ttf", 32);
  }
  void display(Player player, Opponent opponent){
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
  
  void textBox(){
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
  
  void playerInfo(Player player){
    fill(0);
    stroke(0);
    strokeWeight(textWeight/2);
    translate(width/2 - textWeight*2,height/2);
    line(0, boxLength.y*2, boxLength.x,boxLength.y*2);
    triangle(0, boxLength.y*2, boxTextInitX, boxLength.y*2, boxTextInitX, boxLength.y*2 - boxLength.y*.1);
    line(boxLength.x, boxLength.y*2,  boxLength.x, boxLength.y);
    
    textFont(font);
    textSize(boxTextLength);
    text("HP:", boxTextInitX, boxLength.y);
    textSize(boxTextLength*2.5);
    text(player.name, boxTextInitX, boxLength.y-boxTextLength*5);
    text(":L100", boxTextInitX+boxLength.x/4, boxLength.y-boxTextLength*2.5);
    noFill();
    rect(boxTextInitX+boxTextLength*3, boxLength.y - boxTextLength/1.25, boxLength.x-boxTextInitX-boxTextLength*4, boxTextLength, 5,5,5,5);
    fill(105);
    rect(boxTextInitX+boxTextLength*3, boxLength.y - boxTextLength/1.25, (boxLength.x-boxTextInitX-boxTextLength*4)*int(player.currentStats.get("HP"))/int(player.maxStats.get("HP")), boxTextLength, 5,5,5,5);
    fill(0);
    text(int(player.currentStats.get("HP"))+" / "+int(player.maxStats.get("HP")), boxTextInitX+boxLength.x/8, boxLength.y+height*.0625);
        
    translate(-(width/2 - textWeight*2),-height/2);
  }
  
  void opponentInfo(Opponent opponent){
    fill(0);
    stroke(0);
    strokeWeight(textWeight/2);
    translate(textWeight*8,0);
    line(0, boxLength.y*2, boxLength.x,boxLength.y*2);
    triangle(boxLength.x-boxTextInitX, boxLength.y*2, boxLength.x, boxLength.y*2, boxLength.x-boxTextInitX, boxLength.y*2 - boxLength.y*.1);
    line(0, boxLength.y*2,  0, boxLength.y*1.25);
    
    textFont(font);
    textSize(boxTextLength);
    text("HP:", boxTextInitX, boxLength.y*1.75);
    
    textSize(boxTextLength*2.5);
    text(opponent.name, boxTextInitX, boxLength.y*1.75-boxTextLength*5);
    text(":L100", boxTextInitX+boxLength.x/4, boxLength.y*1.75-boxTextLength*2.5);

    noFill();
    rect(boxTextInitX+boxTextLength*3, boxLength.y*1.75 - boxTextLength/1.25, boxLength.x-boxTextInitX-boxTextLength*4, boxTextLength, 5,5,5,5);
    fill(205);
    rect(boxTextInitX+boxTextLength*3, boxLength.y*1.75 - boxTextLength/1.25, (boxLength.x-boxTextInitX-boxTextLength*4)*int(opponent.currentStats.get("HP"))/int(opponent.maxStats.get("HP")), boxTextLength, 5,5,5,5);
    
    translate(-textWeight*8,0);
  }
  void drawText(){
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
    textSize(textLength.y/4.5);
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
  
  void actions(){
    fill(0);
    stroke(0);
    strokeWeight(1);
    textFont(font);
    textSize(textLength.y/6);
    text(player.moves.get(0).name, textInit.x +width*.05, textInit.y+height*.075);
    text(player.moves.get(1).name, textInit.x +width*(.05+textLength.x/(2*width)), textInit.y+height*.075);
    text(player.moves.get(2).name, textInit.x +width*.05, textInit.y+textLength.y-height*.05);
    text(player.moves.get(3).name, textInit.x +width*(.05+textLength.x/(2*width)), textInit.y+textLength.y-height*.05);
    
    if(frameCount%60<15 || frameCount%60>20){
    triangle(textInit.x+width*.01 + cursor[0]*width*textLength.x/(2*width), cursor[1]*height*.075+textInit.y +height*.045, 
             textInit.x+width*.01 + cursor[0]*width*textLength.x/(2*width), cursor[1]*height*.075+textInit.y+textLength.y/6 +height*.05, 
             textInit.x+width*.035 + cursor[0]*width*textLength.x/(2*width), cursor[1]*height*.075+textInit.y+textLength.y/12 +height*.0475);
    }
  }
  
  //Text goes through a buffer, chaniging text based on what's still in the buffer
  //This also changes phases and goes based off of turn order
  void changeText(){
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
