# Intellihome
A simple smart home controller meant to work with homemade automations controlled by the Particle Photon microcontroller.

The Particle Photon is a small Wi-Fi enabled microcontoller structurally similar to the Arduino. The system comes with Particle Cloud, a system used for these boards to communicate and exchange information in the form of events. Along with Particle Cloud comes the Particle SDK, available for use with iOS, Android and Windows. Through this SDK, user-developed applications can be interfaced with these boards. 

#The Application

Intellihome, formerly called Purple Morocco as a working title, is an application written to communicate with and control properly-programmed Photons through a simple process of boards and tasks. Upon startup, Intellihome will send a universal request to all devices on the same account, and every online device will respond with a registration string defining its name and a list of tasks. The user can use Intellihome to select a target board and one of its programmed tasks. When the request is sent, the requested board will receive the message and perform the requested task from its end. A message will be sent back to the user to confirm the success of the task.

#Essential Features
• Logs into the Particle Cloud
• Sends a request for a receives a list of online boards and their tasks
• Displays this information to the user in a simple interface
• Sends requests immediately, at a specified time of day or when the user enters a chosen location
• Displays task statuses to user when tasks have completed

#Timeline
• Began project on September 17, 2016
• Immediate request feature completed on September 23, 2016
• Timed request feature completed within the next few days
• First commit October 18, 2016
• Initially location-aware on October 28, 2016
Followed by a gradual improvement
• Location request feature completed on January 13, 2017
Followed by a gradual, ongoing improvement
