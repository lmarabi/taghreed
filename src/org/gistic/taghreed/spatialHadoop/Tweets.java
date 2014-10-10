/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the
 * NOTICE file distributed with this work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package org.gistic.taghreed.spatialHadoop;

import java.io.DataInput;

import java.io.DataOutput;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.Text;

import edu.umn.cs.spatialHadoop.core.Point;
import edu.umn.cs.spatialHadoop.io.TextSerializerHelper;


/**
 * A data type used to index tweets for Taghreed project.
 * @author Louai Alarabi
 */
public class Tweets extends Point{
	private static final Log LOG = LogFactory.getLog(Tweets.class);
	  public String created_at;
	  public long tweet_id;
	  public long user_id;
	  public String screen_name;
	  public String tweet_text;
	  public int follower_count;
	  public String language;
	  public String osystem;
	

  @Override
  public void write(DataOutput out) throws IOException {
      out.writeUTF(created_at);
      out.writeLong(tweet_id);
      out.writeLong(user_id);
      out.writeUTF(screen_name);
      out.writeUTF(tweet_text);
      out.writeInt(follower_count);
      out.writeUTF(language);
      out.writeUTF(osystem);
      super.write(out);
  }

  @Override
  public void readFields(DataInput in) throws IOException {
      created_at = in.readUTF();
      tweet_id = in.readLong();
      user_id = in.readLong();
      screen_name = in.readUTF();
      tweet_text = in.readUTF();
      follower_count = in.readInt();
      language = in.readUTF();
      osystem = in.readUTF();
      super.readFields(in);
    
  }

  @Override
  public Text toText(Text text) {
	byte[] separator = new String(",").getBytes();
    text.append(created_at.getBytes(), 0, created_at.getBytes().length);
    text.append(separator, 0,separator.length);
    TextSerializerHelper.serializeLong(tweet_id, text, ',');
    TextSerializerHelper.serializeLong(user_id, text, ',');
    text.append(screen_name.getBytes(), 0, screen_name.getBytes().length);
    text.append(separator, 0,separator.length);
    text.append(tweet_text.getBytes(), 0, tweet_text.getBytes().length);
    text.append(separator, 0,separator.length);
    TextSerializerHelper.serializeInt(follower_count, text, ',');
    text.append(language.getBytes(), 0, language.getBytes().length);
    text.append(separator, 0,separator.length);
    text.append(osystem.getBytes(), 0, osystem.getBytes().length);
    text.append(separator, 0,separator.length);
    super.toText(text);
    return text;
  }

  @Override
  public void fromText(Text text) {
	  String[] list = text.toString().split(",");
	  created_at = list[0];
	  tweet_id = Long.parseLong(list[1]);
	  user_id = Long.parseLong(list[2]);
	  screen_name = list[3];
	  tweet_text = list[4];
	  follower_count = Integer.parseInt(list[5]);
	  language = list[6];
	  osystem = list[7];
	  super.fromText(new Text(list[8]+","+list[9]));
	  
  }

  @Override
  public Tweets clone() {
    Tweets c = new Tweets();
    c.created_at = this.created_at;
    c.tweet_id = this.tweet_id;
    c.user_id  = this.user_id;
    c.screen_name = this.screen_name;
    c.tweet_text = this.tweet_text;
    c.follower_count = this.follower_count;
    c.language = this.language;
    c.osystem = this.osystem;
    super.set(x, y);
    return c;
  }

}
