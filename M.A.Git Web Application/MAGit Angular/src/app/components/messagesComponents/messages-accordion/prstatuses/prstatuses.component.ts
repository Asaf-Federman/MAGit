import { Component, OnInit, Input } from '@angular/core';
import { PRStatus } from 'src/app/models/PRStatus';

@Component({
  selector: 'app-prstatuses',
  templateUrl: './prstatuses.component.html',
  styleUrls: ['./prstatuses.component.css']
})
export class PRStatusesComponent implements OnInit {
  @Input() prStatus:PRStatus;
 
  constructor() { }

  ngOnInit() {
  }

  checkIfDeclineMessage(){
    if(this.prStatus){
      return 'declineMessage' in this.prStatus;
    }

    return false;
  }

}
