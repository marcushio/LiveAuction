#Distrubuted Auction
@author: Jaime Yang
@author: Colton Trujillo
@author: Marcus Trujillo

### Introduction
This is an auction that is done in a distributed fashion. It consists of 3 primary
programs working together, a Bank, an AuctionHouse, and an Agent. 

### Usage__ 
#### Bank Usage
The first thing that must be done to run the program is to Start the bank.
When you start the bank, it opens an RMI registry programmatically, so that clients
will be able to invoke methods remotely. 

#### AuctionHouse usage 
-AuctionHouses can have multiple auctions going on at once. We've made items have 
a rarity system as follows... 

Item Rarity:
- inferior = 1
- ordinary = 2
- uncommon = 3
- rare = 4
- unique = 5
- superior = 6
- legendary = 7

#### Agent Usage 
The agent has a GUI that the user can operate. when starting the agent, there are
3 command line requirements. In the following order

Initial Balance, name of agent, and the address/name of the server we'll be connecting to. 

-The initial balance must be a positive number that fits within the bounds of a double. 
-The name of the agent can be as many names as needed so you can put a first/last name. You 
can also just put a first name. 
-In our particular case we're naming our bank as "bankServer". 


### Project Assumptions 
We'll just list some assumptions. 
-We're setting up all clients with a positive bank account balance. 

### Design Choices 
We used RMI to handle our remote communication rather than managing all the port
programming ourselves. This implied that we'd have to make our individual programs 
threadsafe. 

### Docs 


### Known issues 

### Testing and debugging





