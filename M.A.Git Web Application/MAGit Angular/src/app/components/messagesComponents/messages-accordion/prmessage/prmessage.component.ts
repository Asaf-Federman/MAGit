import { Component, OnInit, Input } from '@angular/core';
import { PRBasicMessage } from 'src/app/models/PRBasicMessage';

@Component({
  selector: 'app-prmessage',
  templateUrl: './prmessage.component.html',
  styleUrls: ['./prmessage.component.css']
})
export class PRMessageComponent implements OnInit {
 
  @Input() prMessage:PRBasicMessage

  ngOnInit(): void {
  }
}
