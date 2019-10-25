import { Component, OnInit, Input } from '@angular/core';
import { DeletedBranchMessage } from 'src/app/models/DeletedBranchMessage';

@Component({
  selector: 'app-delete-message',
  templateUrl: './delete-message.component.html',
  styleUrls: ['./delete-message.component.css']
})
export class DeleteMessageComponent implements OnInit {
  @Input() deletedMessage:DeletedBranchMessage

  constructor() { }

  ngOnInit() {
  }

}
