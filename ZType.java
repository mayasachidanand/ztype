import java.awt.Color;
import java.util.Random;
import javalib.funworld.*;
import javalib.worldimages.TextImage;
import tester.Tester;

interface IConstantValues {
  int HEIGHT = 400;
  int WIDTH = 600;
}

// represents a ZType World
class ZTypeWorld extends World implements IConstantValues {
  ILoWord wordList;
  Random rand;

  ZTypeWorld(ILoWord wordList, Random rand) {
    this.wordList = wordList;
    this.rand = rand;
  }

  ZTypeWorld(ILoWord wordList) {
    this(wordList, new Random());
  }

  //renders the state of this ZTypeWorld
  public WorldScene makeScene() {
    WorldScene ws = new WorldScene(WIDTH, HEIGHT);
    return this.wordList.draw(ws);
  }

  //handles key events for this ZTypeWorld
  public World onKeyEvent(String key) {
    if (this.wordList.hasActiveWord() && key.equals(key)) {
      return new ZTypeWorld(this.wordList.checkAndReduce(key)
          .filterOutEmpties().switchToActiveFirst());
    }
    else if (!this.wordList.hasActiveWord() && key.equals(key)) {
      return new ZTypeWorld(this.wordList.switchToActiveFirst().checkAndReduceFirst(key)
          .filterOutEmpties().switchToActiveFirst());
    }
    else {
      return this;
    }
  }

  // handles on tick events for this ZType World
  public World onTick() {
    if (this.wordList.hitsBottom()) {
      return this.endOfWorld("A message");
    }
    else {
      return new ZTypeWorld(this.randomGenerator(), this.rand);
    }
  }

  // produces the last scene of the world when the world stops
  public WorldScene lastScene(String s) {
    return new WorldScene(WIDTH, HEIGHT)
        .placeImageXY(new TextImage("Game Over!", 40, Color.black), 300, 200);
  }

  // returns the word list with a random word added to the end in a random location
  public ILoWord randomGenerator() {
    return this.wordList.moveList().addToEnd(
        new InactiveWord(
            new Rando().randomWords(this.rand.nextInt(26), ""),
            ((this.rand.nextInt(6) * 50) + 200),
            0));
  }
}

//represents a list of words
interface ILoWord {

  // return a new list where all words beginning with the given string have
  // first letter removed
  ILoWord checkAndReduce(String s);

  ILoWord checkAndReduceFirst(String s);

  // add the given IWord to the end of the list 
  ILoWord addToEnd(IWord w);

  // return a list without any IWords that have empty strings
  ILoWord filterOutEmpties();

  // draw all the words in the list onto the world scene
  WorldScene draw(WorldScene ws);

  // return the list with all items shifted down 
  ILoWord moveList();

  // return true if any of the words are at the bottom of the screen
  boolean hitsBottom();

  // make the first item in the list an active word 
  ILoWord switchToActiveFirst();

  // check if the list has an active word
  boolean hasActiveWord();
}

//represents an empty list of words
class MtLoWord implements ILoWord {
  /* TEMPLATE
   * Methods:
   * ... checkAndReduce(String) ...  -- ILoWord
   * ... checkAndReduce1(String) ...  -- ILoWord
   * ... addToEnd(IWord) ...  -- ILoWord
   * ... filterOutEmpties() ...  -- ILoWord
   * ... draw(WorldScene) ...  -- WorldScene
   * ... moveList() ... ILoWord
   * ... hitsBottom() ... boolean
   * ... switchToActiveFirst() ... ILoWord
   * ... hasActiveWord() ... boolean
   */

  //return a new list where all words beginning with the given string have
  // first letter removed, return empty list
  public ILoWord checkAndReduce(String s) {
    return this;
  }

  // return a new list where all words beginning with the given string have
  // first letter removed, return empty list
  public ILoWord checkAndReduceFirst(String s) {
    return this;
  }

  // add the given IWord to the end of the list 
  public ILoWord addToEnd(IWord w) {
    /* TEMPLATE:
     * Methods on parameters:
     * ... w.substringCondition(String) ... --  boolean
     * ... w.substringHelper() ...  -- IWord
     * ... w.filterHelper() ...  -- boolean
     * ... w.placeWord(WorldScene) ...  -- WorldScene
     */
    return new ConsLoWord(w, this);
  }

  // remove any IWords from the list that have an empty string, 
  // return empty list
  public ILoWord filterOutEmpties() {
    return this;
  }

  // draw items in list on world scene, 
  // list is empty, so return given world scene
  public WorldScene draw(WorldScene ws) {
    return ws;
  }

  // move the items in the list down 20 pixels
  // list is empty, so return given list 
  public ILoWord moveList() {
    return this;
  }

  // return false, an empty list can't hit the bottom of the screen
  public boolean hitsBottom() {
    return false;
  }

  // return this list, there isn't a first to be switched to active 
  public ILoWord switchToActiveFirst() {
    return this;
  }

  // there are no IWords in an empty list, so there are no active words
  public boolean hasActiveWord() {
    return false;
  }
}

// represents a cons list of words 
class ConsLoWord implements ILoWord {
  IWord first;
  ILoWord rest;

  ConsLoWord(IWord first, ILoWord rest) {
    this.first = first;
    this.rest = rest;
  }

  /* TEMPLATE
   * Fields:
   * ... this.first ...  -- IWord
   * ... this.rest ...  -- ILoWord
   * 
   * Methods:
   * ... checkAndReduce(String) ...  -- ILoWord
   * ... checkAndReduce1(String) ...  -- ILoWord
   * ... addToEnd(IWord) ...  -- ILoWord
   * ... filterOutEmpties() ...  -- ILoWord
   * ... draw(WorldScene) ...  -- WorldScene
   * ... moveList() ...  -- ILoWord
   * ... hitsBottom() ...  -- boolean
   * ... switchToActiveFirst() ...  -- ILoWord
   * ... hasActiveWord() ...  -- boolean
   *  
   * Methods for Fields:
   * ... this.first.substringCondition(s) ...  -- boolean
   * ... this.first.substringHelper() ...  -- IWord
   * ... this.rest.checkAndReduce(s) ...  -- ILoWord
   * ... this.rest.addToEnd(w) ...  -- ILoWord
   * ... this.first.filterHelper() ...  -- boolean
   * ... this.rest.filterOutEmpties() ...  -- ILoWord
   * ... this.rest.draw(this.first.placeWord(ws)) ...  -- WorldScene
   * ... this.first.moveWord() ...  -- IWord
   * ... this.rest.moveList() ...  -- ILoWord
   * ... this.first.bottomWord() ...  -- boolean
   * ... this.rest.hitsBottom() ... -- boolean
   * ... this.first.makeActive() ...  -- IWord
   * ... this.first.isActive() ...  -- boolean
   * ... this.rest.hasActiveWord() ...  -- boolean
   */

  // return a list where any words that begin with the same letter as the given string 
  // have the first letter removed from the word
  public ILoWord checkAndReduce(String s) {
    if (this.first.substringCondition(s)) {
      return new ConsLoWord(this.first.substringHelper(), this.rest.checkAndReduce(s));
    }
    else {
      return new ConsLoWord(this.first, this.rest.checkAndReduce(s));
    }
  }

  // return a list where if the first word begins with the same letter as the given string, 
  // the first letter is removed and the rest of the list stays the same 
  public ILoWord checkAndReduceFirst(String s) {
    if (this.first.substringCondition(s)) {
      return new ConsLoWord(this.first.substringHelper(), this.rest);
    }
    else {
      return this;
    }
  }

  // add the given IWord to the end of this list 
  public ILoWord addToEnd(IWord w) {
    /* TEMPLATE:
     * Methods on parameters:
     * ... w.substringCondition(String s) ... --  boolean
     * ... w.substringHelper() ...  -- IWord
     * ... w.filterHelper() ...  -- boolean
     * ... w.placeWord(WorldScene ws) ...  -- WorldScene
     */
    return new ConsLoWord(this.first, this.rest.addToEnd(w));
  }

  // return a list where any IWords that have empty strings are removed 
  public ILoWord filterOutEmpties() {
    if (this.first.filterHelper()) {
      return this.rest.filterOutEmpties();
    }
    else {
      return new ConsLoWord(this.first, this.rest.filterOutEmpties());
    }
  }

  // draw the list of words onto the world scene
  public WorldScene draw(WorldScene ws) {
    return this.rest.draw(this.first.placeWord(ws));
  }

  // move the positions of the items in the list down 20 pixels
  public ILoWord moveList() {
    return new ConsLoWord(this.first.moveWord(), this.rest.moveList());
  }

  // does any item in the list hit the bottom of the screen 
  public boolean hitsBottom() {
    return this.first.bottomWord() 
        || this.rest.hitsBottom();
  }

  // return a new list of words where the first in the list is active
  // and the rest in the list remains the same 
  public ILoWord switchToActiveFirst() {
    return new ConsLoWord(this.first.makeActive(), this.rest);
  }

  // does this list of words have an active word?
  public boolean hasActiveWord() {
    return this.first.isActive() 
        || this.rest.hasActiveWord();
  }
}

// represents random word function generator
class Rando {
  
  String randomWords(int i, String s) {
    if (s.length() < 6 && s.length() >= 1) {
      return randomWords(new Random().nextInt(26), 
          s + "abcdefghijklmnopqrstuvwxyz".substring(i, i + 1));
    }
    else if (s.length() < 1) {
      return randomWords(new Random().nextInt(26), 
          "abcdefghijklmnopqrstuvwxyz".substring(i, i + 1));
    }
    else {
      return s;
    }
  }
}

//represents a word in the ZType game
interface IWord {

  // check whether the first letter of the word is equal to the given string
  boolean substringCondition(String s);

  // return a new active word without the first letter 
  IWord substringHelper();

  // check whether the string in an active word is equal to an empty string
  boolean filterHelper();

  // draw the word onto the world scene
  WorldScene placeWord(WorldScene ws);

  // move the word down in pixel coordinates
  IWord moveWord();

  // is this word at he bottom of the game
  boolean bottomWord();

  // return this IWord as an active word
  IWord makeActive();

  // is this word an Active word
  boolean isActive();
}

// represents an abstract class of IWords
abstract class AWord implements IWord {
  String word;
  int x;
  int y;

  AWord(String word, int x, int y) {
    this.word = word;
    this.x = x;
    this.y = y;
  }

  //is the first letter of the word the same as the given string?
  public boolean substringCondition(String s) {
    if (this.filterHelper()) {
      return false;
    }
    else {
      return this.word.toLowerCase().substring(0,1).equals(s);
    }
  }

  //return a new active word without the first letter 
  public abstract IWord substringHelper();

  //is the string in the active word an empty string?
  public boolean filterHelper() {
    return this.word.equals("");
  }

  //draw the word onto the world scene
  public abstract WorldScene placeWord(WorldScene ws);

  // return a new world scene with the word moved down 20 pixel coordinates
  public abstract IWord moveWord();

  // check whether the word is at the bottom of the screen 
  public boolean bottomWord() {
    return this.y >= 400;
  }

  // returns this IWord as an active word 
  public abstract IWord makeActive();

  // determines whether or not a IWord is active 
  public abstract boolean isActive();
}

//represents an active word in the ZType game
class ActiveWord extends AWord {

  ActiveWord(String word, int x, int y) {
    super(word, x, y);
  }

  /* TEMPLATE
   * Fields:
   * ... this.word ...  -- String
   * ... this.x ...  -- int
   * ... this.y ...  -- int
   * 
   * Methods:
   * ... substringCondition(String s) ...  -- boolean
   * ... substringHelper() ...  -- IWord
   * ... filterHelper() ...  -- boolean
   * ... placeWord() ...  -- WorldScene
   * ... moveWord() ...  -- IWord
   * ... bottomWord() ...  -- boolean
   * ... makeActive() ...  -- IWord
   * ... isActive() ...  -- boolean
   */

  // return a new active word with the first letter of the word removed
  public IWord substringHelper() {
    return new ActiveWord(this.word.substring(1), this.x, this.y);
  }

  // place the word as an image onto the world scene
  public WorldScene placeWord(WorldScene ws) {
    return ws.placeImageXY(new TextImage(this.word, 20, Color.blue), this.x, this.y);
  }

  // return a new world scene with the word moved down 20 pixel coordinates 
  public IWord moveWord() {
    return new ActiveWord(this.word, this.x, this.y + 20);
  }

  // return this, this IWord is already active 
  public IWord makeActive() {
    return this;
  }

  // determine whether this IWord is active, return true since it is in 
  // the active word class
  public boolean isActive() {
    return true;
  }
}

//represents an inactive word in the ZType game
class InactiveWord extends AWord {

  InactiveWord(String word, int x, int y) {
    super(word, x, y);
  }

  /* TEMPLATE
   * Fields:
   * ... this.word ...  -- String
   * ... this.x ...  -- int
   * ... this.y ...  -- int
   * 
   * Methods:
   * ... substringCondition(String s) ...  -- boolean
   * ... substringHelper() ...  -- IWord
   * ... filterHelper() ...  -- boolean
   * ... placeWord() ...  -- WorldScene
   * ... moveWord() ...  -- IWord
   * ... bottomWord() ...  -- boolean
   * ... makeActive() ...  -- IWord
   * ... isActive() ...  -- boolean
   */

  // return this inactive word
  public IWord substringHelper() {
    return this;
  }

  //place the word as an image onto the world scene
  public WorldScene placeWord(WorldScene ws) {
    return ws.placeImageXY(new TextImage(this.word, 20, Color.red), this.x, this.y);
  }

  // return a new world scene with the word moved down 20 pixel coordinates
  public IWord moveWord() {
    return new InactiveWord(this.word, this.x, this.y + 20);
  }

  // make this inactive word an active word
  public IWord makeActive() {
    return new ActiveWord(this.word, this.x, this.y);
  }

  // determine whether this word is active, return false because 
  // it is in the inactive class
  public boolean isActive() {
    return false;
  }
}

//all examples and tests for ILoWord
class ExamplesWordLists implements IConstantValues {

  IWord activea = new ActiveWord("apple", 300, 50);
  IWord activea1 = new ActiveWord("apple", 300, 70);
  IWord activeb = new ActiveWord("Axe", 200, 50);
  IWord activec = new ActiveWord("banana", 300, 50);
  IWord actived = new ActiveWord("", 3, 450);
  IWord activee = new ActiveWord("sleep", 100, 400);
  IWord activef = new ActiveWord("Ant", 300, 50);
  IWord inactivea = new InactiveWord("Ant", 300, 50);
  IWord inactivea1 = new InactiveWord("Ant", 300, 70);
  IWord inactiveb = new InactiveWord("bee", 400, 50);
  IWord inactiveb1 = new InactiveWord("bee", 400, 70);
  IWord inactivec = new InactiveWord("cat", 200, 50);
  IWord inactived = new InactiveWord("shiipa", 200, 0);
  WorldScene wsa = new WorldScene(500, 500);
  WorldScene ws1 = new WorldScene(WIDTH, HEIGHT);
  WorldScene wsb = wsa.placeImageXY(new TextImage("apple", 20, Color.blue), 300, 50);
  WorldScene wsb1 = ws1.placeImageXY(new TextImage("apple", 20, Color.blue), 300, 50);
  WorldScene wsc = wsa.placeImageXY(new TextImage("Ant", 20, Color.red), 300, 50);
  WorldScene wsd = ws1.placeImageXY(new TextImage("Game Over!", 40, Color.black), 300, 200);
  ILoWord mtlist = new MtLoWord();
  ILoWord lista = new ConsLoWord(this.activea, this.mtlist);
  ILoWord listb = new ConsLoWord(this.activeb, this.lista);
  ILoWord listc = new ConsLoWord(this.activea, new ConsLoWord(this.activeb, this.mtlist));
  ILoWord listd = new ConsLoWord(this.actived, this.lista);
  ILoWord liste = new ConsLoWord(this.inactivea, this.mtlist);
  ILoWord listf = new ConsLoWord(this.inactiveb, this.liste);
  ILoWord listg = new ConsLoWord(this.inactivea, this.lista);
  ILoWord list1 = new ConsLoWord(this.activea, this.mtlist);
  ILoWord list2 = new ConsLoWord(this.inactivea, this.list1);
  ILoWord list3 = new ConsLoWord(this.activeb, this.list2);
  ILoWord list4 = new ConsLoWord(this.activec, this.list3);
  ILoWord list5 = new ConsLoWord(this.activee, this.list1);
  ILoWord list6 = new ConsLoWord(this.inactivea, new ConsLoWord(this.activeb, this.mtlist));
  ILoWord list7 = new ConsLoWord(this.activea1, new ConsLoWord(this.inactived, this.mtlist));
  ILoWord list8 = new ConsLoWord(this.inactiveb, this.mtlist);
  ZTypeWorld z = new ZTypeWorld(this.list8);


  // tests substringCondition method
  boolean testSubstringCondition(Tester t) {
    return t.checkExpect(this.activea.substringCondition("a"), true)
        && t.checkExpect(this.inactiveb.substringCondition("a"), false)
        && t.checkExpect(this.activeb.substringCondition("a"), true)
        && t.checkExpect(this.actived.substringCondition("a"), false);
  }

  // tests substringHelper method
  boolean testSubstringHelper(Tester t) {
    return t.checkExpect(this.inactivea.substringHelper(), this.inactivea)
        && t.checkExpect(this.activea.substringHelper(), new ActiveWord("pple", 300, 50))
        && t.checkExpect(this.activeb.substringHelper(), new ActiveWord("xe", 200, 50));
  }

  // tests checkAndReduce method
  boolean testCheckAndReduce(Tester t) {
    return t.checkExpect(this.mtlist.checkAndReduce("a"), this.mtlist)
        && t.checkExpect(this.lista.checkAndReduce("a"), new ConsLoWord(
            new ActiveWord("pple", 300, 50), this.mtlist));
  }

  // tests addToEnd method 
  boolean testAddToEnd(Tester t) {
    return t.checkExpect(this.mtlist.addToEnd(this.activea), new ConsLoWord(
        this.activea, this.mtlist))
        && t.checkExpect(this.lista.addToEnd(this.activeb), new ConsLoWord(
            this.activea, new ConsLoWord(this.activeb, this.mtlist)));
  }

  // tests filterHelper
  boolean testFilterHelper(Tester t) {
    return t.checkExpect(this.activea.filterHelper(), false)
        && t.checkExpect(this.actived.filterHelper(), true)
        && t.checkExpect(this.inactivea.filterHelper(), false);
  }

  // tests filterOutEmpties method 
  boolean testFilter(Tester t) {
    return t.checkExpect(this.mtlist.filterOutEmpties(), this.mtlist)
        && t.checkExpect(this.lista.filterOutEmpties(), this.lista)
        && t.checkExpect(this.listd.filterOutEmpties(), this.lista);
  }

  // tests placeWord method 
  boolean testPlaceWord(Tester t) {
    return t.checkExpect(this.activea.placeWord(wsa), new WorldScene(500, 500)
        .placeImageXY(new TextImage("apple", 20, Color.blue), 300, 50))
        && t.checkExpect(this.inactivea.placeWord(wsa), new WorldScene(500, 500)
            .placeImageXY(new TextImage("Ant", 20, Color.red), 300, 50));
  }

  // tests draw method
  boolean testwordDraw(Tester t) {
    return t.checkExpect(this.mtlist.draw(wsa), this.wsa)
        && t.checkExpect(this.lista.draw(wsa), wsb)
        && t.checkExpect(this.liste.draw(wsa), wsc);
  }

  // tests moveList method 
  boolean testMoveList(Tester t) {
    return t.checkExpect(this.mtlist.moveList(), this.mtlist)
        && t.checkExpect(this.list1.moveList(), new ConsLoWord(this.activea1, this.mtlist))
        && t.checkExpect(this.listf.moveList(), new ConsLoWord(this.inactiveb1, 
            new ConsLoWord(this.inactivea1, this.mtlist)));
  }

  // tests moveWord method 
  boolean testMoveWord(Tester t) {
    return t.checkExpect(this.activea.moveWord(), this.activea1)
        && t.checkExpect(this.inactivea.moveWord(), this.inactivea1);
  }

  // tests makeActive method 
  boolean testMakeActive(Tester t) {
    return t.checkExpect(this.activea.makeActive(), this.activea)
        && t.checkExpect(this.inactivea.makeActive(), new ActiveWord("Ant", 300, 50));
  }

  // tests bottomWord method
  boolean testBottomWord(Tester t) {
    return t.checkExpect(this.activea.bottomWord(), false)
        && t.checkExpect(this.actived.bottomWord(), true)
        && t.checkExpect(this.activee.bottomWord(), true)
        && t.checkExpect(this.inactivea.bottomWord(), false);
  }

  // tests isActive method 
  boolean testIsActive(Tester t) {
    return t.checkExpect(this.activea.isActive(), true)
        && t.checkExpect(this.inactivea.isActive(), false);
  }

  // tests hits bottom method
  boolean testHitsBottom(Tester t) {
    return t.checkExpect(this.list1.hitsBottom(), false)
        && t.checkExpect(this.mtlist.hitsBottom(), false)
        && t.checkExpect(this.list5.hitsBottom(), true);
  }

  // tests switchToActiveFirst method 
  boolean testSwitchToActiveFirst(Tester t) {
    return t.checkExpect(this.mtlist.switchToActiveFirst(), this.mtlist)
        && t.checkExpect(this.list1.switchToActiveFirst(), this.list1)
        && t.checkExpect(this.list2.switchToActiveFirst(), 
            new ConsLoWord(this.activef, this.list1));
  }

  // tests hasActiveWord method 
  boolean testHasActiveWord(Tester t) {
    return t.checkExpect(this.mtlist.hasActiveWord(), false)
        && t.checkExpect(this.list1.hasActiveWord(), true)
        && t.checkExpect(this.list2.hasActiveWord(), true)
        && t.checkExpect(this.liste.hasActiveWord(), false);
  }

  // tests randomGenerator method
  boolean testRandomGenerator(Tester t) {
    ZTypeWorld wrld = new ZTypeWorld(this.list1, new Random(2));
    return t.checkExpect(wrld.randomGenerator(), this.list7);
  }

  // tests makeScene method 
  boolean testMakeScene(Tester t) {
    ZTypeWorld wrld = new ZTypeWorld(this.list1);
    ZTypeWorld wrld1 = new ZTypeWorld(this.mtlist);
    return t.checkExpect(wrld.makeScene(), this.wsb1)
        && t.checkExpect(wrld1.makeScene(), this.ws1);
  }

  // tests lastScene method
  boolean testLastScene(Tester t) {
    ZTypeWorld wrld1 = new ZTypeWorld(this.mtlist);
    return t.checkExpect(wrld1.lastScene("Game Over!"), this.wsd);
  }

  //big bang
  boolean testBigBang(Tester t) {
    ZTypeWorld world = new ZTypeWorld(this.list1);
    int worldWidth = WIDTH;
    int worldHeight = HEIGHT;
    double tickRate = 1.0;
    return world.bigBang(worldWidth, worldHeight, tickRate);
  }
}


