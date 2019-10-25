import { Component, OnInit, OnDestroy, Input } from '@angular/core';
import { IForkMessage } from 'src/app/models/IForkMessage';

@Component({
  selector: 'app-fork-message',
  templateUrl: './fork-message.component.html',
  styleUrls: ['./fork-message.component.css']
})
export class ForkMessageComponent implements OnInit {

@Input() forkMessage:IForkMessage
  constructor() { }

  ngOnInit() {
  }
}
