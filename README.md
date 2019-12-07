#Distributed Auction
@author: Jaime Yang
@author: Colton Trujillo
@author: Marcus Trujillo

### Introduction
This is a suite of networked programs that together implement a blind auction. 
We opted for the blind style to prevent cheating. Agents cannot work together toward
any particular outcome if they do not know their opponents bid amounts.

The application consists of 3 primary programs which are based on an bidder, a bank,
and an auction house.  


### Project Assumptions 
-We expect good input as specified here in the readme. 
-Special attention must be paid to the command-line arguments used to run the program and
 textbox input. Specifically, the bid values must be positive decimal numbers.
-We're setting up all clients with a positive bank account balance. 

### Usage

#### General Usage
Run the bank. You may run the agent Gui or Auction Houses after this. If there are
no Auction Houses registered, however, the agent won't be able to do much.
The program is mostly driven by the Auction Houses which have timed blind auctions
and the Agents who can browse auction houses, items, and make bids on items through
entering dollar amounts and clicking bid. The status of the bids are updated live
as they occur!


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
To use the Agent, run the main method in Gui. You must give the following arguments when
running the program. A dollar amount your agent will start with, his or her name,
the ip address of the computer you are running the agent from, and the ip address of 
the bank server in that order and with spaces between each entry.
The following is an example: 1000.00 Bojack 64.106.21.149 64.106.21.148

The agent has a GUI that the user can operate. The layout is organized into columns associated 
with 


### Design Choices 
We used RMI to handle our remote communication rather than managing all the port
programming ourselves. This implied that we'd have to make our individual programs 
threadsafe. 

We opted to have packages for the major classes of Agent, Bank, and AuctionHouse
and the create a Helper package for classes that are used by all three or are 
just general utilities. This really helped with organization and division of labor.

### Division of Labor
Agent package - Colton
Bank package - Marcus
AuctionHouse package - Jaime
Helper package - All three of us
Debugging - All three of us

### Docs 
Docs are in the doc folder as usual.

### Known issues by package 
#### Bank
#### Agent

#### AuctionHouse
Only first bid works
#### Helper
### Testing and debugging
We experimented with JUnit and did a few tests that way. Ultimately we went down to
the lab and tested with one another, as we ran our programs on the network and set
break points when we found issues. 




