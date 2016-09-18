# PokeStat
PokeStat is an app designed for any product supporting Amazon Alexa, which can provide any Pokemon's base stats on demand, including alternate forms, Mega Evolutions, and Primal Reversions! Simply call the app through a phrase like, "Alexa, open PokeStat" and quickly gain access to valuable stat information. PokeStat is written using the Amazon Alexa Skills Kit (ASK) and Java for the AWS Lambda backend.

<br>
<div align="center"><img src="http://sunquyman.xyz/pokestat/img/PokeStatLogo_108x108.png"/></div>
<br>
<br>
## Functionality

PokeStat accepts phrases such as:
* "What are Mega Mewtwo Y's base stats?"
* "What are the base stats of Primal Kyogre?"
* "What base stats does Dragonite have?"

To end PokeStat, just use any of Alexa's default closing or end phrases. Alternatively, PokeStat has the in-built phrases:
* "Thank you."
* "Thank you, PokeStat."
* "That's enough, PokeStat."
which will also trigger its end.
<br>


## File Structure

The file structure is primarily based on the ASK example folder and Maven build structure.

* <b>src/main/java/pokestat: </b> Holds the source files for the project.
⋅⋅* <b>speechAssets: </b> Contains the IntentSchema, utterances file, and slot type information.

* <b>target: </b> Contains the exports of the Maven build, including .jars<br>
* <b>pom.xml: </b> Simply the Maven pom.xml, build information
<br>

## Building 
Navigate to the main directory, /pokestat, and run:
```bash
$ mvn assembly:assembly -DdescriptorId=jar-with-dependencies package
```
<br>

## Future Implementation
* Adding ability to prompt PokeStat for specific stats (e.g. "What is Vaporeon's base HP stat?")
* Pokemon stat analysis function, giving the user a specific Pokemon's stronger stats, its weaker ones, and giving a short analysis on what the user should do if facing the Pokemon.
<br>

-----------------------------------------------------------------------------------------

## Appendix

### A. Pokeapi - Great RESTful Pokemon API, utilized when constructed the database file this application uses: http://pokeapi.co/

### B. Creating a .jar Deployment Package Using Maven - Just as the title says, may help give better idea of file structure: https://docs.aws.amazon.com/lambda/latest/dg/java-create-jar-pkg-maven-no-ide.html

### C. Echosim.io - Useful Alexa testing tool: https://echosim.io/

## Credits

### Website: http://sunquyman.xyz/pokestat/


