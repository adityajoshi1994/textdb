import { Component, ViewChild } from '@angular/core';

import { MockDataService } from './services/mock-data-service';
import { CurrentDataService } from './services/current-data-service';

import { TheFlowchartComponent } from './flowchart/the-flowchart.component';
import { OperatorBarComponent } from './operatorbar/operator-bar.component';

declare var jQuery: any;

@Component({
    moduleId: module.id,
    selector: 'my-app',
    template: `
    <nav the-navbar id="css-navbar" class="navbar navbar-toggleable-md navbar-light bg-faded"></nav>
    <nav operator-bar id="css-operator-bar" class="navbar navbar-toggleable-md navbar-light bg-faded" #theOperatorBar></nav>
		<div id="wrapper">
        <side-bar-container class="container fill"></side-bar-container>
		    <flowchart-container class="container fill" #theFlowchart></flowchart-container>
		</div>
	`,
    providers: [MockDataService, CurrentDataService],
    styleUrls: ['style.css']
})
export class AppComponent {
	name = 'Angular';

    constructor(private currentDataService: CurrentDataService, private mockDataService: MockDataService) { }

    @ViewChild('theFlowchart') theFlowchart: TheFlowchartComponent;
    @ViewChild('theOperatorBar') theOperatorBar: OperatorBarComponent;

    ngAfterViewInit() {
        var current = this;
        
        jQuery(document).ready(function() {
            current.theFlowchart.initialize({});
            current.theOperatorBar.initialize();

        });

    }
}
