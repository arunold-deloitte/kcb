import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-topbar',
  templateUrl: './topbar.html',
  styleUrls: ['./topbar.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TopbarComponent {

}
