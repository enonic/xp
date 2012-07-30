ArrayList tweets = new ArrayList();
ArrayList slots  = new ArrayList();
PFont font = loadFont("Verdana");
int box_w   = 20,     box_h = 10,
    col_gap = 1,      slot_gap = 3,
    brick_start = 90, brick_end = 500,
    slides  = 1,      canvas_x  = 480,
    canvas_y = 550;
float gravita = 0.03;

class Slot{
  color bcolor;
  String language;
  int index;
  int pos_x, last_col;
  ArrayList tw;

  Slot(lan){
    last_col = 0;
    language = lan; index = slots.size();
    prev_slot = slots.get(index-1);
    tw = new ArrayList();
    pos_x    = ( prev_slot == null ? 0 :
                 prev_slot.get_pos_x(prev_slot.last_col)
               ) + box_w + slot_gap;
    slots.add(this);
  }

  int inc(tweet){
    if(floor(tw.size() / 20) > last_col){
      for(int k=index+1; k< slots.size(); k++ )
        slots.get(k).pos_x += (box_w + col_gap);
      last_col ++;
    }
    tw.add(tweet);
    return last_col;
  }

  color get_color(){
    return color(index * 10,125,255);
  }

  int get_pos_x(col){
    return pos_x + (box_w + col_gap) * col;
  }

  void draw(){
    fill(get_color());
    textFont(font, 12);
    textAlign(CENTER);
    text(language, pos_x, brick_end + 20,  get_pos_x(last_col+1) - pos_x, 13);
  }
}

Slot getSlot(lan){
  for(int i=0; i< slots.size(); i++)
    if(((Slot) slots.get(i)).language == lan)
      return ((Slot) slots.get(i));
  return null;
}

class Tweet{
  HashMap tweet;
  int index;
  Slot slot;
  float pos_y, vel_y;
  int slot_index;
  int col_x, frames;

  Tweet(HashMap data, i){
    tweet = data; frames = 0;
    pos_y = brick_start; vel_y = 0;
    slot = getSlot(data.iso_language_code) ||
           new Slot(data.iso_language_code);
    index = i;
    col_x =slot.inc(this);
    slot_index = slot.tw.size() -1;
  }

  void draw(){
    int diff_x = mouseX - slot.get_pos_x(col_x);
    int diff_y = mouseY - pos_y;
    if(mouse_down && 0 < diff_x && diff_x < box_w && 0 < diff_y && diff_y < box_h){
      active_tweet = this;
      mouse_down = false;
    }
    fill(slot.get_color());
    frames = (frames <= 500 && pos_y > brick_start + 30) ? frames + 1 : frames;
    boolean update = (frames < 500);
    if(update){
      for(int k=0; k < slot_index; k++){
        Tweet tw = (Tweet) slot.tw.get(k);
        if(col_x == tw.col_x &&  abs( pos_y - tw.pos_y) < (box_h+2)){
          pos_y  = tw.pos_y - (box_h+1);
          update = false;
          break;
        }
      }
    }
    rect(slot.get_pos_x(col_x), pos_y, box_w, box_h);
    if(update && pos_y < brick_end){
      vel_y  += gravita;
      pos_y  += vel_y;
    }
  }
}

void addTweet(HashMap tweet){
  tweets.add(new Tweet(tweet,tweets.size()));
}

void setup(){
  background(0);
  noStroke();
  colorMode(HSB, 255);
}

void draw(){
  size(canvas_x, canvas_y);
  textFont(font, 18);
  textAlign(LEFT);
  fill(255);
  text("tweet: " + topic, 20, 30);
  textFont(font, 12);
  if(active_tweet){
    fill(active_tweet.slot.get_color());
    text(active_tweet.tweet.from_user + ": " + active_tweet.tweet.text,20, 50, 400, 70);
  }

  for(int i=0; i<tweets.size(); i++){
    ((Tweet) tweets.get(i)).draw();
  }
  for(int i=0; i<slots.size(); i++){
    ((Slot) slots.get(i)).draw();
  }

  if(slots.size() > 0){
    Slot last_slot = ((Slot) slots.get(slots.size()-1));
    if(last_slot.get_pos_x(last_slot.last_col) > (canvas_x * slides) - 10){
      tweets = new ArrayList();
      slots  = new ArrayList();
    }
  }
}

boolean mouse_down = false;
Tweet active_tweet = null;

void mousePressed(){
  mouse_down = true;
}