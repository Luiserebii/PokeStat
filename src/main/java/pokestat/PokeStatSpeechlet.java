/**
====================================================================
PokeStat Speechlet class, containing most of the logic for PokeStat
====================================================================
 */
package pokestat;

import java.util.Map;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;


public class PokeStatSpeechlet implements Speechlet {
    //private static final Logger log = LoggerFactory.getLogger(PokeStatSpeechlet.class);

    @Override
    public void onSessionStarted(final SessionStartedRequest request, final Session session)
            throws SpeechletException {
        //log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(),
          //      session.getSessionId());
        // any initialization logic goes here
    }

    @Override
    public SpeechletResponse onLaunch(final LaunchRequest request, final Session session)
            throws SpeechletException {
        //log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(),
        //        session.getSessionId());
        return getWelcomeResponse();
    }

    @Override
    public SpeechletResponse onIntent(final IntentRequest request, final Session session)
            throws SpeechletException {
        //log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
         //       session.getSessionId());

        // Get intent from the request object.
        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;

        // Note: If the session is started with an intent, no welcome message will be rendered;
        // rather, the intent specific response will be returned.
        if ("BaseStatIntent".equals(intentName)) {
            return getBaseStat(intent, session);

	} else if ("AMAZON.CancelIntent".equals(intentName) || "AMAZON.StopIntent".equals(intentName)) {
	    return getSpeechletResponse("Thanks for using PokeStat! Enjoy Sun and Moon when it arrives!", "", false);        
	} else {
            throw new SpeechletException("Invalid Intent");
        }
    }

    @Override
    public void onSessionEnded(final SessionEndedRequest request, final Session session)
            throws SpeechletException {
        //log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(),
         //       session.getSessionId());
        // any cleanup logic goes here
    }

    /**
     * Creates and returns a {@code SpeechletResponse} with a welcome message.
     *
     * 
     */
    private SpeechletResponse getWelcomeResponse() {
        // Create the welcome message.
        String speechText =
                "Welcome to PokeStat. Please give me a Pokemon by "
                        + "saying, what are Charizard's base stats ";
        String repromptText =
                "Please give me a Pokemon by saying, what are Charizard's base stats ";

        return getSpeechletResponse(speechText, repromptText, true);
    }

    /**
     * Creates a {@code SpeechletResponse} for the intent and returns the selected Pokemon's
     * base stats, in order of HP, Atk, Def, Sp. Atk, Sp. Def, Spe
     *
     * @param intent
     *            intent for the request
     * @return SpeechletResponse spoken and visual response the given intent
     */
    private SpeechletResponse getBaseStat(final Intent intent, final Session session) {
        // Get the slots from the intent.
        Map<String, Slot> slots = intent.getSlots();

        // Get the pokemonName slot from the list of slots.
        Slot pokemonNameSlot = slots.get("PokemonName");
        String speechText, repromptText;

        if (pokemonNameSlot != null) {
            // Get the pokemonName from the slot and apply logic
            String pokemonName = pokemonNameSlot.getValue();
	    pokemonName = purifyName(pokemonName.toLowerCase());
            String pokeStats = searchCSV(pokemonName);

	    if(!pokeStats.equals("")) {
	    	String[] pokeStatArr = pokeStats.split(","); 
           	speechText = pokemonName + "'s base stats are " + pokeStatArr[0] + ", " + pokeStatArr[1] + ", " + pokeStatArr[2] + ", " + pokeStatArr[3] + ", " + pokeStatArr[4] + ", " + pokeStatArr[5];
            	repromptText = speechText;
	    //If nothing appears, set message to unknown message
	    } else {
		speechText = "I'm not sure what Pokemon you mentioned... the name I received was: " + pokemonName;
           	repromptText =
                    "I'm not sure what Pokemon base stats you wanted. You can prompt me through, "
                            + "what are Volcarona's base stats?";		
	    }

        } else {
            // Render an error since we don't know what the users favorite color is.
            speechText = "I'm not sure what Pokemon you mentioned...";
            repromptText =
                    "I'm not sure what Pokemon base stats you wanted. You can prompt me through, "
                            + "what are Volcarona's base stats?";
        }

        return getSpeechletResponse(speechText, repromptText, true);
    }

	private String searchCSV(String pokemonName){

		String pokeStats = "";
		try {
			URL pokeCSVURL = new URL("http://sunquyman.xyz/pokestat/data/pokestatdata.csv");
			Scanner sc = new Scanner(pokeCSVURL.openStream());
			String currLine = null;
			String currPokemon;
			while(sc.hasNext()){
				currLine = sc.nextLine();
				currPokemon = currLine.substring(0, currLine.indexOf(','));
				if(currPokemon.equals(pokemonName)){
					pokeStats = currLine.substring(currLine.indexOf(',') + 1);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pokeStats;

	}

	private String purifyName(String pokemonName){

		//Chop off 's, delete any . in the string
		if(pokemonName.indexOf("'s") != -1) {
			int index = pokemonName.indexOf("'s");
			pokemonName = pokemonName.substring(0, index);
		}
		if(pokemonName.indexOf(".") != -1) {
			int index = pokemonName.indexOf(".");
			StringBuilder sb = new StringBuilder(pokemonName);
			sb.deleteCharAt(index);
			pokemonName = sb.toString();
		}
		//If there is some kind of whitespace, it must be something special... so parse for it
		if(pokemonName.indexOf(" ") != -1) {
			//If it is mega...
			if(pokemonName.indexOf("mega") != -1){
				if(pokemonName.indexOf("mega") == 0){
					pokemonName = pokemonName.substring(5);
					//If we have a special mega (like mega-x)
					if(pokemonName.indexOf(" ") != -1){
						int index = pokemonName.indexOf(" ");
						pokemonName = pokemonName.substring(0, index) + " mega " + pokemonName.substring(index + 1);
					} else {
						pokemonName = pokemonName + "-mega";
					}
				}
				//Now that alt. mega format is in place, can use general operation...
				pokemonName = pokemonName.replaceAll(" ", "-");
				//If it is primal...
			} else if(pokemonName.indexOf("primal") != -1){
				if(pokemonName.indexOf("primal") == 0){
					int index = pokemonName.indexOf("primal");
					pokemonName = pokemonName.substring(7) + "-primal";
				} else {
					pokemonName = pokemonName.replaceAll(" ", "-");
				}
			} else { //must be some kind of form...
				//chop off any "form" endings
				if(pokemonName.indexOf("form") != -1){
					int index = pokemonName.indexOf("form");
					pokemonName = pokemonName.substring(0,index - 1);
				}
				pokemonName = pokemonName.replaceAll(" ", "-");
			}
		}
		return pokemonName;
	}

    /**
     * Returns a Speechlet response for a speech and reprompt text.
     */
    private SpeechletResponse getSpeechletResponse(String speechText, String repromptText,
            boolean isAskResponse) {
        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("PokeStat");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        if (isAskResponse) {
            // Create reprompt
            PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
            repromptSpeech.setText(repromptText);
            Reprompt reprompt = new Reprompt();
            reprompt.setOutputSpeech(repromptSpeech);

            return SpeechletResponse.newAskResponse(speech, reprompt, card);

        } else {
            return SpeechletResponse.newTellResponse(speech, card);
        }
    }
}
